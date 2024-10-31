package com.absint.astree;

import edu.hm.hafner.analysis.IssueParser;
import edu.hm.hafner.analysis.IssueBuilder;
import edu.hm.hafner.analysis.ParsingException;
import edu.hm.hafner.analysis.ReaderFactory;
import edu.hm.hafner.analysis.Report;
import edu.hm.hafner.analysis.Severity;

/**
 * Parser for AbsInt Astree XML reports.
 */
public class AstreeReportParser extends IssueParser {
    /**
     * ID for serialisation
     */
    private static final long serialVersionUID = 1L;

    /**
     * Check if file format is acceptable.
     * 
     * @param readerFactory factory to read XML document
     *
     * @return true if file is acceppted by this parser or false if not
     */
    @Override
    public boolean accepts(final ReaderFactory readerFactory) {
        return isXmlFile(readerFactory);
    }

    /**
     * Parse a AbsInt Astree XML report.
     *
     * @param readerFactory factory to read XML document
     *
     * @return report with parsed information
     */
    @Override
    public Report parse(final ReaderFactory readerFactory) throws ParsingException {
        // create template with basic settings for issues
        IssueBuilder issueBuilder = new IssueBuilder();

        // parse document
        AstreeSimpleReportParser parser = new AstreeSimpleReportParser();
        readerFactory.parse(parser);

        // use project description as reference
        // FIXME(ivan): I have no idea why, that's what the previous version of the code did...
        issueBuilder.setReference(parser.getProjectDescription());

        // create new report
        Report report = new Report();
        report.setOriginReportFile(readerFactory.getFileName());

        final StringBuilder descriptionBuilder = new StringBuilder();
        final StringBuilder categoryBuilder = new StringBuilder();

        // process the messages
        for (Message message : parser.getMessages()) {
            // build description out of code snippet
            final String code = parser.getCodeSnippet(message.locationID);
            if (code != null && !code.isEmpty()) {
                descriptionBuilder.append("<p>Code:</p><pre>");
                descriptionBuilder.append(code);
                descriptionBuilder.append("</pre>");
            }

            // build description out of context
            final String context = message.context;
            if (context != null && !context.isEmpty()) {
                descriptionBuilder.append("<p>Context:</p><pre>");
                descriptionBuilder.append(context);
                descriptionBuilder.append("</pre>");
            }

            // build category out of message type and category
            final AlarmType type = parser.getAlarmType(message.typeID);
            if (type == null) {
                throw new ParsingException("Missing finding category " + message.typeID);
            }
            final String category = parser.getCategory(type.categoryID);
            if (category == null) {
                throw new ParsingException("Missing finding group " + type.categoryID);
            }
            categoryBuilder.append(type.type);
            categoryBuilder.append(" [");
            categoryBuilder.append(category);
            categoryBuilder.append(']');

            // retrieve location of message
            Location location = parser.getLocation(message.locationID);
            if(location == null)
                location = new Location();

            // create new issue
            issueBuilder.setMessage(message.text)
                .setFileName(parser.getFile(location.fileID))
                .setLineStart(location.startLine)
                .setLineEnd(location.endLine)
                .setColumnStart(location.startColumn)
                .setColumnEnd(location.endColumn)
                .setCategory(categoryBuilder.toString())
                .setDescription(descriptionBuilder.toString())
                .setSeverity(message.severity);

            // add issue to report
            report.add(issueBuilder.build());

            descriptionBuilder.setLength(0);
            categoryBuilder.setLength(0);
        }

        issueBuilder.close();

        return report;
    }
}
