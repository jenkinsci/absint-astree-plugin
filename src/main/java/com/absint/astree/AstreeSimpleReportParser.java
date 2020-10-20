package com.absint.astree;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;


/**
 * Parser which simply parses Astree XML reports into data structures whithout
 * interconnecting the elements
 */
public class AstreeSimpleReportParser {
    /**
     * parsed alarm categories
     */
    private Map<String, String> m_categories = new HashMap<String, String>();
  
    /**
     * parsed alarm types
     */
    private Map<String, AlarmType> m_types = new HashMap<String, AlarmType>();

    /**
     * parsed code snippets
     */
    private Map<String, String> m_codeSnippets = new HashMap<String, String>();
  
    /**
     * parsed files
     */
    private Map<String, String> m_files = new HashMap<String, String>();
  
    /**
     * parsed locations
     */
    private Map<String, Location> m_locations = new HashMap<String, Location>();

    /**
     * parsed messages
     */
    private List<Message> m_messages = new ArrayList<Message>();

    /**
     * parse document into internal data structures
     *
     * @param doc document to parse
     */
    public void parse(Document doc) {
        // parse and build report for all message types
        parseMessages(doc, Message.MessageType.Alarm);
        parseMessages(doc, Message.MessageType.Error);
        parseMessages(doc, Message.MessageType.Note);

        // parse additional information needed by messages
        parseLocations(doc);
        parseFiles(doc);
        parseCodeSnippets(doc);
        parseAlarmTypes(doc);
        parseAlarmCategories(doc);
    }

    /**
     * clear all parsed data
     */
    public void clear() {
        m_messages.clear();
        m_locations.clear();
        m_files.clear();
        m_codeSnippets.clear();
        m_types.clear();
        m_categories.clear();
    }

    /**
     * get parsed messages
     *
     * @return parsed messages
     */
    public List<Message> getMessages() {
        return m_messages;
    }
    
    /**
     * get parsed locations
     *
     * @return parsed locations
     */
    public Map<String, Location> getLocations() {
        return m_locations;
    }

    /**
     * get parsed files
     *
     * @return parsed files
     */
    public Map<String, String> getFiles() {
        return m_files;
    }

    /**
     * get parsed code snippets
     *
     * @return parsed files
     */
    public Map<String, String> getCodeSnippets() {
        return m_codeSnippets;
    }

    /**
     * get parsed alarm types
     *
     * @return parsed alarm types
     */
    public Map<String, AlarmType> getAlarmTypes() {
        return m_types;
    }    
 
    /**
     * get parsed alarm categories
     *
     * @return parsed alarm categories
     */
    public Map<String, String> getCategories() {
        return m_categories;
    }   

    /**
     * parse all messages from specific tpye out of report
     *
     * @param doc report to parse
     * @param type message type to parse
     */
    private void parseMessages(Document doc, Message.MessageType type) {
        NodeList messages = doc.getElementsByTagName(type.toString());
        for (int i = 0; i < messages.getLength(); i++) {
            Element messageElement = (Element)messages.item(i);
            Message message = new Message();
 
            // message text
            NodeList lines = messageElement.getElementsByTagName("textline");
            StringBuilder messageText = new StringBuilder();
            for (int y = 0; y < lines.getLength(); y++) {
                Element line = (Element)lines.item(y);
                if (0 < messageText.length()) {
                    messageText.append("<br>");
                }
                messageText.append(line.getTextContent());
            }
 
            message.setLocationID(messageElement.getAttribute("location_id"))
                .setTypeID(messageElement.getAttribute("type"))
                .setType(type)
                .setContext(messageElement.getAttribute("context"))
                .setText(messageText.toString());
            m_messages.add(message);
        }
    }

    /**
     * parse all locations out of report
     *
     * @param doc report to parse
     */
    private void parseLocations(Document doc) {
        NodeList locations = doc.getElementsByTagName("location");
        for (int i = 0; i < locations.getLength(); i++) {
            Element locationElement = (Element)locations.item(i);
            Location location = new Location();
            
            // location information
            location.setLineStart(locationElement.getAttribute("p_start_line"))
                .setLineEnd(locationElement.getAttribute("p_end_line"))
                .setColStart(locationElement.getAttribute("p_start_col"))
                .setColEnd(locationElement.getAttribute("p_end_col"));

            // original location information
            location.setOrigLineStart(locationElement.getAttribute("o_start_line"))
                .setOrigLineEnd(locationElement.getAttribute("o_end_line"))
                .setOrigColStart(locationElement.getAttribute("o_start_col"))
                .setOrigColEnd(locationElement.getAttribute("o_end_col"));

            // retrieve file information
            location.setFileID(locationElement.getAttribute("p_file"))
                .setOrigFileID(locationElement.getAttribute("o_file"));

            m_locations.put(locationElement.getAttribute("id"), location);
        }
    }
     
    /**
     * parse all files out of report
     *
     * @param doc report to parse
     */
    private void parseFiles(Document doc) {
        // find all files and add it
        NodeList files = doc.getElementsByTagName("file");
        for (int i = 0; i < files.getLength(); i++) {
            Element file = (Element)files.item(i);

            m_files.put(file.getAttribute("id"), file.getAttribute("name"));
        }
    }

    /**
     * parse all code snippets out of report
     *
     * @param doc report to parse
     */
    private void parseCodeSnippets(Document doc) {
        // find all code snippets and add it
        NodeList snippets = doc.getElementsByTagName("code-snippet");
        for (int i = 0; i < snippets.getLength(); i++) {
            Element snippet = (Element)snippets.item(i);

            // code lines
            StringBuilder code = new StringBuilder();
            NodeList lines = snippet.getElementsByTagName("line");
            for (int y = 0; y < lines.getLength(); y++) {
                Element line = (Element)lines.item(y);
                if (0 < code.length()) {
                    code.append("<br>");
                }
                code.append(line.getTextContent());
            }

            m_codeSnippets.put(snippet.getAttribute("location_id"), code.toString());
        }
    }

    /**
     * parse all alarm types out of report
     *
     * @param doc report to parse
     */
    private void parseAlarmTypes(Document doc) {
        // find all alarm types and add it
        NodeList alarmTypes = doc.getElementsByTagName("alarm_type");
        for (int i = 0; i < alarmTypes.getLength(); i++) {
            Element alarmType = (Element)alarmTypes.item(i);

            AlarmType type = new AlarmType();
            type.setCategoryID(alarmType.getAttribute("category_id"))
                .setAlarmClass(alarmType.getAttribute("class"))
                .setType(alarmType.getTextContent());
            m_types.put(alarmType.getAttribute("id"), type);
        }
    }

    /**
     * parse all alarm categories out of report
     *
     * @param doc report to parse
     */
    private void parseAlarmCategories(Document doc) {
        // find all categories and add it
        NodeList alarmCategories = doc.getElementsByTagName("alarm_category");
        for (int i = 0; i < alarmCategories.getLength(); i++) {
            Element alarmCategory = (Element)alarmCategories.item(i);

            m_categories.put(alarmCategory.getAttribute("id"), 
                    alarmCategory.getTextContent());
        }
    }
}
