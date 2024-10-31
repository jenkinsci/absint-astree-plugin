package com.absint.astree;

import edu.hm.hafner.analysis.Severity;

/**
 * message
 */
public class Message {
    public String locationID;
    public String typeID;
    public String context;
    public String text;
    public Severity severity = Severity.ERROR;
};
