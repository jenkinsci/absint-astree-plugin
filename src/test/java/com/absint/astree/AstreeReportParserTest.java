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
        assertEquals(4, report.size());
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
        Report alarms = report.filter(Issue.bySeverity(Severity.WARNING_HIGH));
        
        // check if all alarms are in the report
        assertEquals(2, alarms.size());

        // check alarm1
        Issue alarm1 = alarms.get(0);
        assertEquals(Severity.WARNING_HIGH, alarm1.getSeverity());
        assertEquals("Reserved identifier [Failed coding rule checks]", alarm1.getCategory());
        assertEquals("File3.h", alarm1.getFileName());
        assertEquals(51, alarm1.getLineStart());
        assertEquals(51, alarm1.getLineEnd());
        assertEquals(8, alarm1.getColumnStart());
        assertEquals(25, alarm1.getColumnEnd());
        assertEquals("ALARM (R): check reserved-identifier failed (violates M2012.21.1-required)",
                alarm1.getMessage());
        assertEquals("test", alarm1.getReference());
        assertTrue(alarm1.getDescription()
                .replaceAll("&nbsp;", " ")
                .contains("#define _SW_TYPES_H_<br>" +
                    "        ~~~~~~~~~~~~~~~~~"));

        // check alarm2
        Issue alarm2 = alarms.get(1);
        assertEquals(Severity.WARNING_HIGH, alarm2.getSeverity());
        assertEquals("Essential arithmetic conversion [Failed coding rule checks]", alarm2.getCategory());
        assertEquals("C:/dir/File1.i", alarm2.getFileName());
        assertEquals(1841, alarm2.getLineStart());
        assertEquals(1841, alarm2.getLineEnd());
        assertEquals(18, alarm2.getColumnStart());
        assertEquals(27, alarm2.getColumnEnd());
        assertEquals("[ the essential operand types are unsigned char and signed char<br>" +
                "ALARM (R): check essential-arithmetic-conversion failed (violates M2012.10.4-required)", 
                alarm2.getMessage());
        assertEquals("test", alarm2.getReference());
        assertTrue(alarm2.getDescription()
                .replaceAll("&nbsp;", " ")
                .contains("for ((i = 0); (i < (2)); (i++))<br>" +
                    "              ~~~~~~~~~"));

    }

    /**
     * Tests if {@link AstreeReportParser} parses all errors correct in given test XML report
     */
    @Test
    public void checkErrors() {
        AstreeReportParser parser = new AstreeReportParser();

        // parse test file
        Report report = parser.parse(new FileReaderFactory(getResourceAsFile("analysis_report.xml").toPath()));

        // retrieve all errors from report
        Report errors = report.filter(Issue.bySeverity(Severity.ERROR));
        
        // check if all errors are in the report
        assertEquals(1, errors.size());

        // check error1
        Issue error1 = errors.get(0);
        assertEquals(Severity.ERROR, error1.getSeverity());
        assertEquals("Definite runtime error [Errors]", error1.getCategory());
        assertEquals("dir/File2.i", error1.getFileName());
        assertEquals(974, error1.getLineStart());
        assertEquals(974, error1.getLineEnd());
        assertEquals(8, error1.getColumnStart());
        assertEquals(24, error1.getColumnEnd());
        assertEquals("ERROR: Definite runtime error during assignment in this context. Analysis stopped for this context.", 
                error1.getMessage());
        assertEquals("test", error1.getReference());
        assertTrue(error1.getDescription()
                .replaceAll("&nbsp;", " ")
                .contains("l3532#call#Reset_Handler,l3533#call#STARTUP_initDataBSS,l3534#loop=1/1"));
    }

    /**
     * Tests if {@link AstreeReportParser} parses all notes correct in given test XML report
     */
    @Test
    public void checkNotes() {
        AstreeReportParser parser = new AstreeReportParser();

        // parse test file
        Report report = parser.parse(new FileReaderFactory(getResourceAsFile("analysis_report.xml").toPath()));

        // retrieve all notes from report
        Report notes = report.filter(Issue.bySeverity(Severity.WARNING_LOW));
        
        // check if all notes are in the report
        assertEquals(1, notes.size());

        // check note1
        Issue note1 = notes.get(0);
        assertEquals(Severity.WARNING_LOW, note1.getSeverity());
        assertEquals("Other [Errors]", note1.getCategory());
        assertEquals("File3.h", note1.getFileName());
        assertEquals(51, note1.getLineStart());
        assertEquals(51, note1.getLineEnd());
        assertEquals(8, note1.getColumnStart());
        assertEquals(25, note1.getColumnEnd());
        assertEquals("NOTE: Suspicious here!", note1.getMessage());
        assertEquals("test", note1.getReference());
        assertTrue(note1.getDescription()
                .replaceAll("&nbsp;", " ")
                .contains("#define _SW_TYPES_H_<br>" +
                    "        ~~~~~~~~~~~~~~~~~"));
    }

    /**
     * Retrieve file from resource.
     */
    private File getResourceAsFile(String name) {
        URL fileUrl = getClass().getResource(name);
        
        return new File(fileUrl.getFile());
    }

}

