package com.absint.astree;

/**
 * message
 */
public class Message {
    /**
     * possible message types
     */
    public enum MessageType {
        Alarm ("alarm_message"),
        Error ("error_message"),
        Note ("note_message");

        /**
         * name of message type
         */
        private final String name;

        /**
         * c'tor
         *
         * @param s string of enum type
         */
        private MessageType(String s) {
            name = s;
        }

        /**
         * check if enum is equal
         *
         * @param otherName name to compare with
         *
         * @return true if equal false if not
         */
        public boolean equalsName(String otherName) {
            return name.equals(otherName);
        }

        /**
         * convert enumt to string
         */
        public String toString() {
            return this.name;
        }
    }

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
    private MessageType m_messageType = MessageType.Error;

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

    /**
     * set message type
     *
     * @param type message type
     *
     * @return this
     */
    public Message setType(MessageType type) {
        m_messageType = type;
        return this;
    }

    /**
     * get message type
     *
     * @return message type
     */
    public MessageType getType() {
        return m_messageType;
    }
};
