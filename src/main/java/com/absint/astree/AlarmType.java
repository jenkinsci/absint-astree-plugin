package com.absint.astree;

/**
 * alarm type of an AbsInt issue
 */
public class AlarmType {
    /**
     * ID of corresponding category
     */
    private String m_categoryID = "";

    /**
     * alarm type
     */
    private String m_type = "";

    /**
     * set alarm category ID
     */
    public AlarmType setCategoryID(String id) {
        m_categoryID = id;
        return this;
    }

    /**
     * get alarm category ID
     *
     * @return return category ID
     */
    public String getCategoryID() {
        return m_categoryID;
    }

    /**
     * set type
     *
     * @param type alarm type
     *
     * @return this
     */
    public AlarmType setType(String type) {
        m_type = type;
        return this;
    }

    /**
     * get type
     *
     * @return type
     */
    public String getType() {
        return m_type;
    }
}
