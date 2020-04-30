package com.absint.astree;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

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
        
        IssueBuilder issueBuilder = new IssueBuilder();
        Report report = new Report();

        // find all alarms
        NodeList alarms = doc.getElementsByTagName("alarm_message");
        for (int i = 0; i < alarms.getLength(); i++) {
            Element alarm = (Element)alarms.item(i);

            // alarm message
            NodeList lines = alarm.getElementsByTagName("textline");
            String alarmMessage = "";
            for (int y = 0; y < lines.getLength(); y++) {
                Element line = (Element)lines.item(y);
                if (!alarmMessage.isEmpty()) {
                    alarmMessage += "\n";
                }
                alarmMessage += line.getTextContent();
            }

            // get location ID
            String locationID = alarm.getAttribute("location_id");

            // add code snippet to alarm message
            String code = getCodeSnippet(doc, locationID);
            if (!code.isEmpty()) {
                alarmMessage += "\n" + code;
            }

            // get alarm type
            String alarmType = getAlarmType(doc, alarm.getAttribute("type"));

            ///@TODO get rule_description -> description 

            // retrieve location of alarm
            Location location = getLocation (doc, locationID);

            // create new issue
            issueBuilder.setMessage(alarmMessage)
                .setFileName(location.getFileName())
                .setLineStart(location.getLineStart())
                .setLineEnd(location.getLineEnd())
                .setColumnStart(location.getColStart())
                .setColumnEnd(location.getColEnd())
                .setCategory(alarmType)
                .setSeverity(new Severity("ALARM"));

            // add issue to report
            report.add(issueBuilder.build());
        }

        ///@TODO find all errors
        ///@TODO find all notes

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
        String type = new String();

        // search for alarm type with alarmTypeID
        NodeList alarmTypes = doc.getElementsByTagName("alarm_type");
        for (int i = 0; i < alarmTypes.getLength(); i++) {
            Element alarmType = (Element)alarmTypes.item(i);
            if (alarmType.getAttribute("id").equals(alarmTypeID)) {
                type = alarmType.getTextContent();
                ///@TODO retrieve alarm_category and add it to alarm type
                break;
            }
        }
        
        return type;
    }
}
