package com.absint.astree;

/**
 * alarm type of an AbsInt issue
 */
public class AlarmType {
    /** 
     * possible alarm classes
     */
    enum enClass {
        A,
        B,
        C,
        D,
        E,
        R,
        Unknown
    }

    /**
     * ID of corresponding category
     */
    private String m_categoryID = "";

    /**
     * corresponding alarm class
     */
    private enClass m_class = enClass.Unknown;

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
     * set alarm class
     *
     * @param alarmClass alarm class as string
     *
     * @return this
     */
    public AlarmType setAlarmClass(String alarmClass) {
        m_class = enClass.valueOf(alarmClass);
        return this;
    }

    /**
     * set alarm class
     *
     * @param alarmClass set alarm class
     *
     * @return this
     */
    public AlarmType setAlarmClass(enClass alarmClass) {
        m_class = alarmClass;
        return this;
    }

    /**
     * get alarm class
     *
     * @return alarm class as string
     */
    public String getAlarmClassStr() {
        return m_class.name();
    }

    /**
     * get alarm class
     * 
     * @return alarm class as string
     */
    public enClass getAlarmClass() {
        return m_class;
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
