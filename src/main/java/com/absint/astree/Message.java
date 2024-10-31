package com.absint.astree;

import edu.hm.hafner.analysis.Severity;

/**
 * message
 */
public class Message {
    /**
     * ID of corresponding location
     */
    private String m_locationID = "";

    /**
     * ID of corresponding type
     */
    private String m_typeID = "";

    /**
     * message context
     */
    private String m_context = "";

    /**
     * message text
     */
    private String m_text = "";

    /**
     * message type
     */
    private Severity m_severity = Severity.ERROR;

    /**
     * set location ID
     *
     * @param id location ID
     *
     * @return this
     */
    public Message setLocationID(String id) {
        m_locationID = id;
        return this;
    }

    /**
     * get location ID
     *
     * @return location ID
     */
    public String getLocationID() {
        return m_locationID;
    }

    /**
     * set type ID
     *
     * @param id type ID
     *
     * @return this
     */
    public Message setTypeID(String id) {
        m_typeID = id;
        return this;
    }

    /**
     * get type ID
     *
     * @return type ID
     */
    public String getTypeID() {
        return m_typeID;
    }

    /**
     * set message text
     *
     * @param text message text
     *
     * @return this
     */
    public Message setText(String text) {
        m_text = text;
        return this;
    }

    /**
     * get message text
     *
     * @return message text
     */
    public String getText() {
        return m_text;
    }

    /**
     * set message context
     *
     * @param context message context
     *
     * @return this
     */
    public Message setContext(String context) {
        m_context = context;
        return this;
    }

    /**
     * get message context
     *
     * @return message context
     */
    public String getContext() {
        return m_context;
    }

    public void setSeverity(Severity severity) {
        m_severity = severity;
    }

    public Severity getSeverity() {
        return m_severity;
    }
};
