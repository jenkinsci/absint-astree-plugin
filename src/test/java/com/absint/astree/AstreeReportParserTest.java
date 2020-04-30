package com.absint.astree;

import java.io.File;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import com.absint.astree.AstreeReportParser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import org.junit.Test;

import edu.hm.hafner.analysis.FileReaderFactory;
import edu.hm.hafner.analysis.Issue;
import edu.hm.hafner.analysis.ReaderFactory;
import edu.hm.hafner.analysis.Report;
import edu.hm.hafner.analysis.Severity;

/**
 * Tests for {@link AstreeReportParser}.
 */
public class AstreeReportParserTest {

    /**
     * Tests if {@link AstreeReportParser} only accepts XML reports.
     */
    @Test
    public void shouldOnlyAcceptXmlFiles() {
        AstreeReportParser parser = new AstreeReportParser();

        // test if report is accepted
        assertTrue(parser.accepts(new FileReaderFactory(getResourceAsFile("analysis_report.xml").toPath())));
        
        // test if text file rejected
        assertFalse(parser.accepts(new FileReaderFactory(getResourceAsFile("analysis_report.txt").toPath())));
    }

    /**
     * Tests if {@link AstreeReportParser} parses given test XML report completely
     */
    @Test
    public void parsedComplete() {
        AstreeReportParser parser = new AstreeReportParser();

        // parse test file
        Report report = parser.parse(new FileReaderFactory(getResourceAsFile("analysis_report.xml").toPath()));

        // check for completeness
        assertEquals(2, report.size());
    }
    
    /**
     * Tests if {@link AstreeReportParser} parses all alarms correct in given test XML report
     */
    @Test
    public void checkAlarms() {
        AstreeReportParser parser = new AstreeReportParser();

        // parse test file
        Report report = parser.parse(new FileReaderFactory(getResourceAsFile("analysis_report.xml").toPath()));

        // retrieve all alarms from report
        Report warnings = report.filter(Issue.bySeverity(new Severity("ALARM")));
        
        // check if all alarms are in the report
        assertEquals(2, warnings.size());

        ///@TODO add more tests here
    }

    /**
     * Retrieve file from resource.
     */
    private File getResourceAsFile(String name) {
        URL fileUrl = getClass().getResource(name);
        
        return new File(fileUrl.getFile());
    }

}

