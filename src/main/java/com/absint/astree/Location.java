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
     * start line number in the file
     */
    private String m_startLine = "";

    /**
     * end line number in the file
     */
    private String m_endLine = "";

    /**
     * start column number in the file
     */
    private String m_startCol = "";

    /**
     * end column number in the file
     */
    private String m_endCol = "";

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
}
