package com.absint.astree;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import edu.hm.hafner.analysis.Issue;
import edu.hm.hafner.analysis.IssueParser;
import edu.hm.hafner.analysis.IssueBuilder;
import edu.hm.hafner.analysis.ParsingException;
import edu.hm.hafner.analysis.ReaderFactory;
import edu.hm.hafner.analysis.Report;
import edu.hm.hafner.analysis.Severity;

import com.absint.astree.Location;


/**
 * Parser for AbsInt Astree XML reports.
 */
public class AstreeReportParser extends IssueParser {
    private static final long serialVersionUID = 1L;
 
    /**
     * Check if file format is acceptable.
     */
    @Override
    public boolean accepts(final ReaderFactory readerFactory) {
        return isXmlFile(readerFactory);
    }
    
    /**
     * Parse a AbsInt Astree XML report.
     */
    @Override
    public Report parse(final ReaderFactory readerFactory) throws ParsingException {
        Document doc = readerFactory.readDocument();
        
        Report report = new Report();

        // use project description as reference
        String reference = "";
        NodeList projects = doc.getElementsByTagName("project");
        if (projects.getLength() > 0) {
            Element project = (Element)projects.item(0);
            reference = project.getAttribute("description");
        }

        // create template with basic settings for issues
        IssueBuilder issueBuilder = new IssueBuilder();
        issueBuilder.setReference(reference);

        // add all alarms
        issueBuilder.setSeverity(new Severity("ALARM"));
        report.addAll(getMessages(doc, "alarm_message", issueBuilder.build()));
 
        // add all errors
        issueBuilder.setSeverity(Severity.ERROR);
        report.addAll(getMessages(doc, "error_message", issueBuilder.build()));
        
        // add all notes
        issueBuilder.setSeverity(new Severity("NOTE"));
        report.addAll(getMessages(doc, "note_message", issueBuilder.build()));

        return report;
    }

    /**
     * get all messages of given tag name and classify them with given severity
     */
    Report getMessages(Document doc, String tagName, Issue issue) {
        IssueBuilder issueBuilder = new IssueBuilder();
        issueBuilder.copy(issue);
        Report report = new Report();

        // find all messages with tag name
        NodeList messages = doc.getElementsByTagName(tagName);
        for (int i = 0; i < messages.getLength(); i++) {
            Element message = (Element)messages.item(i);

            // message
            NodeList lines = message.getElementsByTagName("textline");
            String messageText = "";
            for (int y = 0; y < lines.getLength(); y++) {
                Element line = (Element)lines.item(y);
                if (!messageText.isEmpty()) {
                    messageText += "\n";
                }
                messageText += line.getTextContent();
            }

            // get location ID
            String locationID = message.getAttribute("location_id");

            // add code snippet to alarm message
            String code = getCodeSnippet(doc, locationID);
            if (!code.isEmpty()) {
                messageText += "\n" + code;
            }

            // get alarm type
            String type = getAlarmType(doc, message.getAttribute("type"));

            //Add context of message to context 
            String description = "";
            if (message.hasAttribute("context")) {
                description = "Context: " + message.getAttribute("context");
            }

            // retrieve location of alarm
            Location location = getLocation (doc, locationID);

            // create new issue
            issueBuilder.setMessage(messageText)
                .setFileName(location.getFileName())
                .setLineStart(location.getLineStart())
                .setLineEnd(location.getLineEnd())
                .setColumnStart(location.getColStart())
                .setColumnEnd(location.getColEnd())
                .setCategory(type)
                .setDescription(description);

            // add issue to report
            report.add(issueBuilder.build());
        }

        return report;
    }


    /**
     * get location information for given location ID
     */
    Location getLocation(Document doc, String locationID) {
        Location location = new Location();

        // search for location with locationID
        NodeList locations = doc.getElementsByTagName("location");
        for (int i = 0; i < locations.getLength(); i++) {
            Element locationElement = (Element)locations.item(i);
            
            if (locationElement.getAttribute("id").equals(locationID)) {
                // location information
                location.setLineStart(locationElement.getAttribute("p_start_line"));
                location.setLineEnd(locationElement.getAttribute("p_end_line"));
                location.setColStart(locationElement.getAttribute("p_start_col"));
                location.setColEnd(locationElement.getAttribute("p_end_col"));

                // original location information
                location.setOrigLineStart(locationElement.getAttribute("o_start_line"));
                location.setOrigLineEnd(locationElement.getAttribute("o_end_line"));
                location.setOrigColStart(locationElement.getAttribute("o_start_col"));
                location.setOrigColEnd(locationElement.getAttribute("o_end_col"));

                // retrieve file information
                location.setFileName(getFileName(doc, locationElement.getAttribute("p_file")));
                location.setOrigFileName(getFileName(doc, locationElement.getAttribute("o_file")));
                break;
            }
        }

        return location;
    }
 
    /**
     * get file information for given file ID
     */
    String getFileName(Document doc, String fileID) {
        String name = new String();

        // search for location with fileID
        NodeList files = doc.getElementsByTagName("file");
        for (int i = 0; i < files.getLength(); i++) {
            Element file = (Element)files.item(i);

            if (file.getAttribute("id").equals(fileID)) {
                name = file.getAttribute("name");
                break;
            }
        }
        
        return name;
    }

    /**
     * get code snippet for given location ID
     */
    String getCodeSnippet(Document doc, String locationID) {
        String code = new String();
        
        // search for code snippet with fileID
        NodeList snippets = doc.getElementsByTagName("code-snippet");
        for (int i = 0; i < snippets.getLength(); i++) {
            Element snippet = (Element)snippets.item(i);
            
            if (snippet.getAttribute("location_id").equals(locationID)) {
                NodeList lines = snippet.getElementsByTagName("line");
                for (int y = 0; y < lines.getLength(); y++) {
                    Element line = (Element)lines.item(y);
                    if (!code.isEmpty())
                        code += "\n";
                    code += line.getTextContent();
                }
                break;
            }
        }

        return code;
    }

    /**
     * get alarm type for given type ID
     */
    String getAlarmType(Document doc, String alarmTypeID) {
        String type = "";

        // search for alarm type with alarmTypeID
        NodeList alarmTypes = doc.getElementsByTagName("alarm_type");
        for (int i = 0; i < alarmTypes.getLength(); i++) {
            Element alarmType = (Element)alarmTypes.item(i);
            if (alarmType.getAttribute("id").equals(alarmTypeID)) {
                type = alarmType.getTextContent();
                String categoryID = alarmType.getAttribute("category_id");
                
                // retrieve alarm_category and add it to alarm type
                NodeList alarmCategories = doc.getElementsByTagName("alarm_category");
                for (int y = 0; y < alarmCategories.getLength(); y++) {
                    Element alarmCategory = (Element)alarmCategories.item(y);
                    if (alarmCategory.getAttribute("id").equals(categoryID)) {
                        type += " (" + alarmCategory.getTextContent() + ")";
                    }
                }
                
                break;
            }
        }
        
        return type;
    }
}
