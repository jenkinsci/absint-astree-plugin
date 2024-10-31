package com.absint.astree;

import org.apache.commons.text.StringEscapeUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import edu.hm.hafner.analysis.Severity;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

/**
 * Parser which simply parses Astree XML reports into data structures whithout
 * interconnecting the elements
 */
public class AstreeSimpleReportParser extends DefaultHandler {
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

    private String projectDescription;

    private String currentId;
    private Message currentMessage;
    private AlarmType currentAlarmType;
    private boolean collectCurrentCharacters = false;
    private StringBuilder currentCharacters = new StringBuilder();
    private StringBuilder auxiliaryStringBuilder = new StringBuilder();

    /**
     * get parsed messages
     *
     * @return parsed messages
     */
    public List<Message> getMessages() {
        return m_messages;
    }

    /**
     * get parsed location
     *
     * @return parsed location
     */
    public Location getLocation(String id) {
        return m_locations.get(id);
    }

    /**
     * get parsed file
     *
     * @return parsed file
     */
    public String getFile(String id) {
        return m_files.get(id);
    }

    /**
     * get parsed code snippet
     *
     * @return parsed code snippet
     */
    public String getCodeSnippet(String locationId) {
        return m_codeSnippets.get(locationId);
    }

    /**
     * get parsed alarm type
     *
     * @return parsed alarm type
     */
    public AlarmType getAlarmType(String id) {
        return m_types.get(id);
    }

    /**
     * get parsed alarm category
     *
     * @return parsed alarm category
     */
    public String getCategory(String id) {
        return m_categories.get(id);
    }

    public String getProjectDescription() {
        return projectDescription;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (qName.equals("alarm_category") || qName.equals("category_group")) {
            currentId = attributes.getValue("id");
            collectCurrentCharacters = true;
        } else if (qName.equals("alarm_type")) {
            currentId = attributes.getValue("id");
            currentAlarmType = new AlarmType();
            currentAlarmType.categoryID = attributes.getValue("category_id");
            collectCurrentCharacters = true;
        } else if (qName.equals("finding_category")) {
            currentId = attributes.getValue("finding_key");
            currentAlarmType = new AlarmType();
            currentAlarmType.categoryID = attributes.getValue("category_group_id");
            collectCurrentCharacters = true;
        } else if (qName.equals("code-snippet")) {
            currentId = attributes.getValue("location_id");
        } else if (qName.equals("line") || qName.equals("textline")) {
            collectCurrentCharacters = true;
        } else if (qName.equals("file")) {
            m_files.put(attributes.getValue("id"), attributes.getValue("name"));
        } else if (qName.equals("location")) {
            final Location location = new Location();
            location.fileID = attributes.getValue("p_file");
            location.startLine = attributes.getValue("p_start_line");
            location.endLine = attributes.getValue("p_end_line");
            location.startColumn = attributes.getValue("p_start_col");
            location.endColumn = attributes.getValue("p_end_col");
            m_locations.put(attributes.getValue("id"), location);
        } else if (qName.equals("finding")) {
            currentMessage = new Message();
            currentMessage.locationID = attributes.getValue("location_id");
            currentMessage.typeID = attributes.getValue("key");
            currentMessage.context = attributes.getValue("context");
            if (attributes.getValue("kind").equals("alarm")) {
                currentMessage.severity = Severity.WARNING_HIGH;
            } else {
                currentMessage.severity = Severity.ERROR;
            }
        } else if (qName.equals("alarm_message") || qName.equals("error_message") || qName.equals("note_message")) {
            currentMessage = new Message();
            currentMessage.locationID = attributes.getValue("location_id");
            currentMessage.typeID = attributes.getValue("type");
            currentMessage.context = attributes.getValue("context");
            if (qName.equals("alarm_message")) {
                currentMessage.severity = Severity.WARNING_HIGH;
            } else if (qName.equals("error_message")) {
                currentMessage.severity = Severity.ERROR;
            } else {
                currentMessage.severity = Severity.WARNING_LOW;
            }
        } else if (qName.equals("project")) {
            projectDescription = attributes.getValue("description");
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equals("alarm_category") || qName.equals("category_group")) {
            m_categories.put(currentId, currentCharacters.toString());
            currentId = null;
            collectCurrentCharacters = false;
            currentCharacters.setLength(0);
        } else if (qName.equals("alarm_type") || qName.equals("finding_category")) {
            currentAlarmType.type = currentCharacters.toString();
            m_types.put(currentId, currentAlarmType);
            currentId = null;
            currentAlarmType = null;
            collectCurrentCharacters = false;
            currentCharacters.setLength(0);
        } else if (qName.equals("code-snippet")) {
            m_codeSnippets.put(currentId, auxiliaryStringBuilder.toString());
            currentId = null;
            auxiliaryStringBuilder.setLength(0);
        } else if (qName.equals("line") || qName.equals("textline")) {
            if (auxiliaryStringBuilder.length() != 0) {
                if (qName.equals("textline")) {
                    auxiliaryStringBuilder.append("<br>");
                } else {
                    auxiliaryStringBuilder.append('\n');
                }
            }
            auxiliaryStringBuilder.append(StringEscapeUtils.escapeHtml4(currentCharacters.toString()));
            collectCurrentCharacters = false;
            currentCharacters.setLength(0);
        } else if (qName.equals("finding") || qName.equals("alarm_message") || qName.equals("error_message") || qName.equals("note_message")) {
            currentMessage.text = auxiliaryStringBuilder.toString();
            m_messages.add(currentMessage);
            currentMessage = null;
            auxiliaryStringBuilder.setLength(0);
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (collectCurrentCharacters) {
            currentCharacters.append(ch, start, length);
        }
    }
}
