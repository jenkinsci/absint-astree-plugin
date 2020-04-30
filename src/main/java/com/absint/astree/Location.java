package com.absint.astree;

/**
 * location information of an AbsInt issue
 */
public class Location {
    /**
     * name of the file
     */
    private String fileName;

    /**
     * name of the original file
     */
    private String origFileName;

    /**
     * start line number in the file
     */
    private String startLine;

    /**
     * end line number in the file
     */
    private String endLine;

    /**
     * start line number in the original file
     */
    private String origLineStart;

    /**
     * end line number in the original file
     */
    private String origLineEnd;

    /**
     * start column number in the file
     */
    private String startCol;

    /**
     * end column number in the file
     */
    private String endCol;

    /**
     * start column number in the original file
     */
    private String origColStart;

    /**
     * end column number in the original file
     */
    private String origColEnd;

    /**
     * get file name
     */
    public String getFileName() {
        if (null != fileName)
            return fileName;
        else
            return new String("");
    }

    /**
     * set file name
     */
    public void setFileName(String name) {
        fileName = name;
    }
 
    /**
     * get original file name
     */
    public String getOrigfileName() {
        if (null != origFileName)
            return origFileName;
        else
            return new String("");
    }

    /**
     * set original file name
     */
    public void setOrigFileName(String name) {
        origFileName = name;
    }

    /**
     * get start line number in file
     */
    public String getLineStart() {
        if (null != startLine)
            return startLine;
        else
            return new String("");
    }
 
    /**
     * get end line number in file
     */
    public String getLineEnd() {
        if (null != endLine)
            return endLine;
        else
            return new String("");
    }

    /**
     * get original start line number in file
     */
    public String getOrigLineStart() {
        if (null != origLineStart)
            return origLineStart;
        else
            return new String("");
    }

    /**
     * get original end line number in file
     */
    public String getOrigLineEnd() {
        if (null != origLineEnd)
            return origLineEnd;
        else
            return new String("");
    }

    /**
     * set start line number in file
     */
    public void setLineStart(String line) {
        startLine = line;
    }
 
    /**
     * set end line number in file
     */
    public void setLineEnd(String line) {
        endLine = line;
    }

    /**
     * set original start line number in file
     */
    public void setOrigLineStart(String line) {
        origLineStart = line;
    }
 
    /**
     * set original end line number in file
     */
    public void setOrigLineEnd(String line) {
        origLineEnd = line;
    }
 
    /**
     * get start column number in file
     */
    public String getColStart() {
        if (null != startCol)
            return startCol;
        else
            return new String("");
    }
 
    /**
     * get end column number in file
     */
    public String getColEnd() {
        if (null != endCol)
            return endCol;
        else
            return new String("");
    }

    /**
     * get original start column number in file
     */
    public String getOrigColStart() {
        if (null != origColStart)
            return origColStart;
        else
            return new String("");
    }

    /**
     * get original end column number in file
     */
    public String getOrigColEnd() {
        if (null != origColEnd)
            return origColEnd;
        else
            return new String("");
    }

    /**
     * set start column number in file
     */
    public void setColStart(String column) {
        startCol = column;
    }
 
    /**
     * set end column number in file
     */
    public void setColEnd(String column) {
        endCol = column;
    }

    /**
     * set original start line number in file
     */
    public void setOrigColStart(String column) {
        origColStart = column;
    }
 
    /**
     * set original end line number in file
     */
    public void setOrigColEnd(String column) {
        origColEnd = column;
    }

}
