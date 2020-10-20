package com.absint.astree;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

import edu.hm.hafner.analysis.Issue;
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
        // read the document
        Document doc = readerFactory.readDocument();
        
        // create template with basic settings for issues
        IssueBuilder issueBuilder = new IssueBuilder();
        
        // use project description as reference
        String reference = "";
        NodeList projects = doc.getElementsByTagName("project");
        if (projects.getLength() > 0) {
            Element project = (Element)projects.item(0);
            reference = project.getAttribute("description");
        }
        issueBuilder.setReference(reference);

        // parse document
        AstreeSimpleReportParser parser = new AstreeSimpleReportParser();
        parser.parse(doc);

        // create new report
        Report report = new Report();

        // process the messages
        parser.getMessages().stream().forEach((message) -> {
            // get location ID
            String locationID = message.getLocationID();

            // build description out of code snippet
            String description = "";
            String code = parser.getCodeSnippets().get(locationID);
            if (null != code && !code.isEmpty()) {
                description += "<p>Code:<br><code>" + code.replaceAll(" ", "&nbsp;") + "</code></p>";
            }

            // build description out of context
            String context = message.getContext(); 
            if (null != context && !context.isEmpty()) {
                description += "<p>Context:<br><code>" + context.replaceAll(" ", "&nbsp;") + "</code></p>";
            }

            // build category out of message type and category
            AlarmType type = parser.getAlarmTypes().get(message.getTypeID());
            String category = type.getType()  + " [" + parser.getCategories().get(type.getCategoryID()) +"]";

            // retrieve location of message
            Location location = parser.getLocations().get(locationID);
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
                .setFileName(parser.getFiles().get(location.getFileID()))
                .setLineStart(location.getLineStart())
                .setLineEnd(location.getLineEnd())
                .setColumnStart(location.getColStart())
                .setColumnEnd(location.getColEnd())
                .setCategory(category)
                .setDescription(description)
                .setSeverity(severity);

            // add issue to report
            report.add(issueBuilder.build());
        });

        return report;
    }
}
