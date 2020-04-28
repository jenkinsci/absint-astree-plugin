package com.absint.astree;

import edu.hm.hafner.analysis.IssueParser;
import edu.hm.hafner.analysis.ParsingException;
import edu.hm.hafner.analysis.ReaderFactory;
import edu.hm.hafner.analysis.Report;

/**
 * Parser for AbsInt Astree xml reports
 */
public class AstreeReportParser extends IssueParser {
    private static final long serialVersionUID = 1L;
 
    @Override
    public boolean accepts(final ReaderFactory readerFactory) {
        return isXmlFile(readerFactory);
    }
    
    @Override
    public Report parse(final ReaderFactory readerFactory) throws ParsingException {
        Report report = new Report();
        /// @TODO parse astree xml report
        return report;
    }
}
