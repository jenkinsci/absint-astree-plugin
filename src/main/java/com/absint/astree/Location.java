package com.absint.astree;

/**
 * location information of an AbsInt issue
 */
public class Location {
    /**
     * ID of the file
     */
    private String m_fileID = "";

    /**
     * name of the original file
     */
    private String m_origFileID = "";

    /**
     * start line number in the file
     */
    private String m_startLine = "";

    /**
     * end line number in the file
     */
    private String m_endLine = "";

    /**
     * start line number in the original file
     */
    private String m_origLineStart = "";

    /**
     * end line number in the original file
     */
    private String m_origLineEnd = "";

    /**
     * start column number in the file
     */
    private String m_startCol = "";

    /**
     * end column number in the file
     */
    private String m_endCol = "";

    /**
     * start column number in the original file
     */
    private String m_origColStart = "";

    /**
     * end column number in the original file
     */
    private String m_origColEnd = "";

    /**
     * get file ID
     * 
     * @return file ID
     */
    public String getFileID() {
        return m_fileID;
    }

    /**
     * set file ID
     * 
     * @param id file ID
     *
     * @return this
     */
    public Location setFileID(String id) {
        m_fileID = id;
        return this;
    }
 
    /**
     * get original file ID
     *
     * @return original file ID
     */
    public String getOrigFileID() {
        return m_origFileID;
    }

    /**
     * set original file ID
     *
     * @param id set orignal file ID
     *
     * @return this
     */
    public Location setOrigFileID(String id) {
        m_origFileID = id;
        return this;
    }

    /**
     * get start line number in file
     *
     * @return start line number
     */
    public String getLineStart() {
        return m_startLine;
    }
 
    /**
     * get end line number in file
     * 
     * @return end line number
     */
    public String getLineEnd() {
        return m_endLine;
    }

    /**
     * get original start line number in file
     * 
     * @return original start line number
     */
    public String getOrigLineStart() {
        return m_origLineStart;
    }

    /**
     * get original end line number in file
     * 
     * @return original end line number
     */
    public String getOrigLineEnd() {
        return m_origLineEnd;
    }

    /**
     * set start line number in file
     *
     * @param line start line number
     *
     * @return this
     */
    public Location setLineStart(String line) {
        m_startLine = line;
        return this;
    }
 
    /**
     * set end line number in file
     *
     * @param line end line number
     * 
     * @return this
     */
    public Location setLineEnd(String line) {
        m_endLine = line;
        return this;
    }

    /**
     * set original start line number in file
     * 
     * @param line original start line number
     * 
     * @return this
     */
    public Location setOrigLineStart(String line) {
        m_origLineStart = line;
        return this;
    }
 
    /**
     * set original end line number in file
     * 
     * @param line original end line number
     * 
     * @return this
     */
    public Location setOrigLineEnd(String line) {
        m_origLineEnd = line;
        return this;
    }
 
    /**
     * get start column number in file
     *
     * @return start column number
     */
    public String getColStart() {
        return m_startCol;
    }
 
    /**
     * get end column number in file
     * 
     * @return end column number
     */
    public String getColEnd() {
        return m_endCol;
    }

    /**
     * get original start column number in file
     * 
     * @return original start column number
     */
    public String getOrigColStart() {
        return m_origColStart;
    }

    /**
     * get original end column number in file
     * 
     * @return original end column number
     */
    public String getOrigColEnd() {
        return m_origColEnd;
    }

    /**
     * set start column number in file
     * 
     * @param column start column number
     *
     * @return this
     */
    public Location setColStart(String column) {
        m_startCol = column;
        return this;
    }
 
    /**
     * set end column number in file
     * 
     * @param column end column number
     *
     * @return this
     */
    public Location setColEnd(String column) {
        m_endCol = column;
        return this;
    }

    /**
     * set original start line number in file
     * 
     * @param column original start column number
     *
     * @return this
     */
    public Location setOrigColStart(String column) {
        m_origColStart = column;
        return this;
    }
 
    /**
     * set original end line number in file
     * 
     * @param column original end column number
     *
     * @return this
     */
    public Location setOrigColEnd(String column) {
        m_origColEnd = column;
        return this;
    }
}
