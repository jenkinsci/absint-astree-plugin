package com.absint.astree;

import edu.hm.hafner.analysis.IssueBuilder;
import edu.hm.hafner.analysis.IssueParser;
import edu.hm.hafner.analysis.ParsingException;
import edu.hm.hafner.analysis.ReaderFactory;
import edu.hm.hafner.analysis.Report;
import edu.hm.hafner.analysis.Severity;

import org.apache.commons.text.StringEscapeUtils;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.SAXException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        final Handler handler = new Handler();
        readerFactory.parse(handler);

        // use project description as reference
        // FIXME(ivan): I have no idea why, that's what the previous version of the code did...
        issueBuilder.setReference(handler.projectDescription);

        // create new report
        Report report = new Report();
        report.setOriginReportFile(readerFactory.getFileName());

        final StringBuilder descriptionBuilder = new StringBuilder();
        final StringBuilder categoryBuilder = new StringBuilder();

        // process the messages
        for (Message message : handler.messages) {
            // build description out of code snippet
            final String code = handler.codeSnippets.get(message.locationID);
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
            final AlarmType type = handler.types.get(message.typeID);
            if (type == null) {
                throw new ParsingException("Missing finding category " + message.typeID);
            }
            final String category = handler.categories.get(type.categoryID);
            if (category == null) {
                throw new ParsingException("Missing finding group " + type.categoryID);
            }
            categoryBuilder.append(type.type);
            categoryBuilder.append(" [");
            categoryBuilder.append(category);
            categoryBuilder.append(']');

            // retrieve location of message
            Location location = handler.locations.get(message.locationID);
            if (location == null)
                location = new Location();

            // create new issue
            issueBuilder.setMessage(message.text)
                .setFileName(handler.files.get(location.fileID))
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

    private static class Handler extends DefaultHandler {
        public String projectDescription;
        public Map<String, String> categories = new HashMap<String, String>();
        public Map<String, AlarmType> types = new HashMap<String, AlarmType>();
        public Map<String, String> codeSnippets = new HashMap<String, String>();
        public Map<String, String> files = new HashMap<String, String>();
        public Map<String, Location> locations = new HashMap<String, Location>();
        public List<Message> messages = new ArrayList<Message>();
    
        private String currentId;
        private Message currentMessage;
        private AlarmType currentAlarmType;
        private boolean collectCurrentCharacters = false;
        private StringBuilder currentCharacters = new StringBuilder();
        private StringBuilder auxiliaryStringBuilder = new StringBuilder();
    
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
                files.put(attributes.getValue("id"), attributes.getValue("name"));
            } else if (qName.equals("location")) {
                final Location location = new Location();
                location.fileID = attributes.getValue("p_file");
                location.startLine = attributes.getValue("p_start_line");
                location.endLine = attributes.getValue("p_end_line");
                location.startColumn = attributes.getValue("p_start_col");
                location.endColumn = attributes.getValue("p_end_col");
                locations.put(attributes.getValue("id"), location);
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
                categories.put(currentId, currentCharacters.toString());
                currentId = null;
                collectCurrentCharacters = false;
                currentCharacters.setLength(0);
            } else if (qName.equals("alarm_type") || qName.equals("finding_category")) {
                currentAlarmType.type = currentCharacters.toString();
                types.put(currentId, currentAlarmType);
                currentId = null;
                currentAlarmType = null;
                collectCurrentCharacters = false;
                currentCharacters.setLength(0);
            } else if (qName.equals("code-snippet")) {
                codeSnippets.put(currentId, auxiliaryStringBuilder.toString());
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
                messages.add(currentMessage);
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

    private static class AlarmType {
        public String categoryID;
        public String type;
    }

    private static class Message {
        public String locationID;
        public String typeID;
        public String context;
        public String text;
        public Severity severity;
    }

    private static class Location {
        public String fileID;
        public String startLine;
        public String endLine;
        public String startColumn;
        public String endColumn;
    }
}
