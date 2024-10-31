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

        // process the messages
        for (Message message : parser.getMessages()) {
            // get location ID
            String locationID = message.getLocationID();

            // build description out of code snippet
            final StringBuilder description = new StringBuilder();
            final String code = parser.getCodeSnippet(locationID);
            if (code != null && !code.isEmpty()) {
                description.append("<p>Code:</p><pre>");
                description.append(code);
                description.append("</pre>");
            }

            // build description out of context
            final String context = message.getContext(); 
            if (context != null && !context.isEmpty()) {
                description.append("<p>Context:</p><pre>");
                description.append(context);
                description.append("</pre>");
            }

            // build category out of message type and category
            final AlarmType type = parser.getAlarmType(message.getTypeID());
            if (type == null) {
                throw new ParsingException("Missing finding category " + message.getTypeID());
            }
            final String category = parser.getCategory(type.getCategoryID());
            if (category == null) {
                throw new ParsingException("Missing finding group " + type.getCategoryID());
            }
            final StringBuilder categoryBuilder = new StringBuilder();
            categoryBuilder.append(type.getType());
            categoryBuilder.append(" [");
            categoryBuilder.append(category);
            categoryBuilder.append(']');

            // retrieve location of message
            Location location = parser.getLocation(locationID);
            if(location == null)
                location = new Location();

            // map message severity
            Severity severity;
            switch (message.getType()) {
                case Alarm:
                    severity = Severity.WARNING_HIGH;
                    break;
                case Error:
                    severity = Severity.ERROR;
                    break;
                case Note:
                    severity = Severity.WARNING_LOW;
                    break;
                default:
                    severity = Severity.ERROR;
            }

            // create new issue
            issueBuilder.setMessage(message.getText())
                .setFileName(parser.getFile(location.getFileID()))
                .setLineStart(location.getLineStart())
                .setLineEnd(location.getLineEnd())
                .setColumnStart(location.getColStart())
                .setColumnEnd(location.getColEnd())
                .setCategory(categoryBuilder.toString())
                .setDescription(description.toString())
                .setSeverity(severity);

            // add issue to report
            report.add(issueBuilder.build());
        }

        issueBuilder.close();

        return report;
    }
}
