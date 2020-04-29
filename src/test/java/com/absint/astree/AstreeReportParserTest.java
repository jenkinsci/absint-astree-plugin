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
import edu.hm.hafner.analysis.ReaderFactory;

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
     * Retrieve file from resource.
     */
    private File getResourceAsFile(String name) {
        URL fileUrl = getClass().getResource(name);
        
        return new File(fileUrl.getFile());
    }

}

