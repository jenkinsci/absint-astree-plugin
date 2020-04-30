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
        Report alarms = report.filter(Issue.bySeverity(new Severity("ALARM")));
        
        // check if all alarms are in the report
        assertEquals(2, alarms.size());

        // check alarm1
        Issue alarm1 = alarms.get(0);
        assertEquals(new Severity("ALARM"), alarm1.getSeverity());
        assertEquals("Reserved identifier", alarm1.getCategory());
        assertEquals("File3.h", alarm1.getFileName());
        assertEquals(51, alarm1.getLineStart());
        assertEquals(51, alarm1.getLineEnd());
        assertEquals(8, alarm1.getColumnStart());
        assertEquals(25, alarm1.getColumnEnd());
        assertEquals("ALARM (R): check reserved-identifier failed (violates M2012.21.1-required)\n" + 
                "#define _SW_TYPES_H_\n" +
                "        ~~~~~~~~~~~~~~~~~", alarm1.getMessage());

        // check alarm2
        Issue alarm2 = alarms.get(1);
        assertEquals(new Severity("ALARM"), alarm2.getSeverity());
        assertEquals("Essential arithmetic conversion", alarm2.getCategory());
        assertEquals("C:/dir/File1.i", alarm2.getFileName());
        assertEquals(1841, alarm2.getLineStart());
        assertEquals(1841, alarm2.getLineEnd());
        assertEquals(18, alarm2.getColumnStart());
        assertEquals(27, alarm2.getColumnEnd());
        assertEquals("[ the essential operand types are unsigned char and signed char\n" +
                "ALARM (R): check essential-arithmetic-conversion failed (violates M2012.10.4-required)\n" +
                "for ((i = 0); (i < (2)); (i++))\n" +
                "              ~~~~~~~~~", alarm2.getMessage());
    }

    /**
     * Retrieve file from resource.
     */
    private File getResourceAsFile(String name) {
        URL fileUrl = getClass().getResource(name);
        
        return new File(fileUrl.getFile());
    }

}

