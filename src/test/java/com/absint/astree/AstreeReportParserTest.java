package com.absint.astree;

import java.io.File;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import edu.hm.hafner.analysis.FileReaderFactory;
import edu.hm.hafner.analysis.Issue;
import edu.hm.hafner.analysis.Report;
import edu.hm.hafner.analysis.Severity;

/**
 * Tests for {@link AstreeReportParser}.
 */
public class AstreeReportParserTest {
    @Test
    public void test() {
        final File dir = new File(getClass().getResource("").getPath());
        for (File file : dir.listFiles()) {
            if (!file.getName().endsWith(".xml"))
                continue;
            final FileReaderFactory readerFactory = new FileReaderFactory(file.toPath());
            final AstreeReportParser parser = new AstreeReportParser();
            assertTrue(parser.accepts(readerFactory));
            testReport(parser.parse(readerFactory));
        }
    }

    private void testReport(Report report) {
        final File file = new File(report.getOriginReportFile());
        final String fileName = file.getName();

        assertEquals(fileName, 0, report.getDuplicatesSize());

        final Set<String> files = report.getAbsolutePaths();
        if (fileName.compareTo("report-18.10.xml") <= 0) {
            assertEquals(fileName, 2, files.size());
            assertTrue(fileName, files.contains("preprocessed/src/scenarios.c"));
            assertTrue(fileName, files.contains("-"));
        } else if (fileName.compareTo("report-21.04i2.xml") <= 0) {
            assertEquals(fileName, 5, files.size());
            assertTrue(fileName, files.contains("preprocessed/src/astree.cfg"));
            assertTrue(fileName, files.contains("preprocessed/src/filter.c"));
            assertTrue(fileName, files.contains("preprocessed/src/scenarios.c"));
            assertTrue(fileName, files.contains("preprocessed/src/dhry/Proc1.c"));
            assertTrue(fileName, files.contains("preprocessed/src/dhry/Proc7.c"));
        } else {
            assertEquals(fileName, 6, files.size());
            assertTrue(fileName, files.contains("preprocessed/src/state_machine.c"));
            assertTrue(fileName, files.contains("preprocessed/src/astree.cfg"));
            assertTrue(fileName, files.contains("preprocessed/src/filter.c"));
            assertTrue(fileName, files.contains("preprocessed/src/scenarios.c"));
            assertTrue(fileName, files.contains("preprocessed/src/dhry/Proc1.c"));
            assertTrue(fileName, files.contains("preprocessed/src/dhry/Proc7.c"));
        }

        final Set<String> categories = report.getCategories();
        if (fileName.compareTo("report-18.10.xml") <= 0) {
            assertEquals(fileName, 21, categories.size());
        } else if (fileName.compareTo("report-21.04i2.xml") <= 0) {
            assertEquals(fileName, 11, categories.size());
            assertTrue(fileName, categories.contains("Integer division by zero [Division or modulo by zero]"));
            assertTrue(fileName, categories.contains("Use of uninitialized variables [Uninitialized variables]"));
            assertTrue(fileName, categories.contains("Overflow in conversion (with unpredictable result) [Invalid ranges and overflows]"));
            if (fileName.compareTo("report-20.04.xml") <= 0) {
                assertTrue(fileName, categories.contains("Definite runtime error [Errors]"));
            } else {
                assertTrue(fileName, categories.contains("Analysis stopped [Errors]"));
            }
            assertTrue(fileName, categories.contains("Possible overflow upon dereference [Invalid usage of pointers and arrays]"));
            assertTrue(fileName, categories.contains("Assertion failure [Failed or invalid directives]"));
            assertTrue(fileName, categories.contains("Incompatible object pointer conversion [Failed coding rule checks]"));
            assertTrue(fileName, categories.contains("Infinite loop [Data and control flow alarms]"));
            assertTrue(fileName, categories.contains("Control flow anomaly [Data and control flow alarms]"));
            assertTrue(fileName, categories.contains("Out-of-bound array access [Invalid usage of pointers and arrays]"));
            assertTrue(fileName, categories.contains("Overflow in arithmetic [Invalid ranges and overflows]"));
        } else {
            assertEquals(fileName, 12, categories.size());
            assertTrue(fileName, categories.contains("Integer division by zero [Division or modulo by zero]"));
            assertTrue(fileName, categories.contains("Use of uninitialized variables [Uninitialized variables]"));
            assertTrue(fileName, categories.contains("Analysis stopped in critical context [Errors]"));
            assertTrue(fileName, categories.contains("Unbounded loop [Data and control flow alarms]"));
            assertTrue(fileName, categories.contains("Overflow in conversion (with unpredictable result) [Invalid ranges and overflows]"));
            assertTrue(fileName, categories.contains("Possible overflow upon dereference [Invalid usage of pointers and arrays]"));
            assertTrue(fileName, categories.contains("Assertion failure [Failed or invalid directives]"));
            assertTrue(fileName, categories.contains("Incompatible object pointer conversion [Failed coding rule checks]"));
            assertTrue(fileName, categories.contains("Infinite loop [Data and control flow alarms]"));
            assertTrue(fileName, categories.contains("Control flow anomaly [Data and control flow alarms]"));
            assertTrue(fileName, categories.contains("Out-of-bound array access [Invalid usage of pointers and arrays]"));
            if (fileName.compareTo("report-22.10i4.xml") <= 0) {
                assertTrue(fileName, categories.contains("Overflow in arithmetic [Invalid ranges and overflows]"));
            } else {
                assertTrue(fileName, categories.contains("Overflow in arithmetic (with unpredictable result) [Invalid ranges and overflows]"));
            }
        }

        if (fileName.compareTo("report-18.10.xml") <= 0) {
            assertEquals(fileName, 38, report.size());

            final Issue alarm1 = report.get(0);
            assertEquals(fileName, Severity.WARNING_HIGH, alarm1.getSeverity());
            if (fileName.equals("report-18.04.xml")) {
                assertEquals(fileName, "Parameter name [Violation of coding rules]", alarm1.getCategory());
            } else {
                assertEquals(fileName, "Parameter name [Failed coding rule checks]", alarm1.getCategory());
            }
            assertEquals(fileName, "preprocessed/src/scenarios.c", alarm1.getFileName());
            assertEquals(fileName, 20, alarm1.getLineStart());
            assertEquals(fileName, 23, alarm1.getColumnStart());
            assertEquals(fileName, 20, alarm1.getLineEnd());
            assertEquals(fileName, 26, alarm1.getColumnEnd());

            final Issue alarm2 = report.get(33);
            assertEquals(fileName, Severity.WARNING_HIGH, alarm2.getSeverity());
            if (fileName.equals("report-18.04.xml")) {
                assertEquals(fileName, "Unreachable code [Violation of coding rules]", alarm2.getCategory());
            } else {
                assertEquals(fileName, "Unreachable code [Failed coding rule checks]", alarm2.getCategory());
            }
            assertEquals(fileName, "preprocessed/src/scenarios.c", alarm2.getFileName());
            assertEquals(fileName, 134, alarm2.getLineStart());
            assertEquals(fileName, 4, alarm2.getColumnStart());
            assertEquals(fileName, 134, alarm2.getLineEnd());
            assertEquals(fileName, 13, alarm2.getColumnEnd());

            final Issue error1 = report.get(34);
            assertEquals(fileName, Severity.ERROR, error1.getSeverity());
            assertEquals(fileName, "preprocessed/src/scenarios.c", error1.getFileName());
            assertEquals(fileName, "Definite runtime error [Errors]", error1.getCategory());
            assertEquals(fileName, 73, error1.getLineStart());
            assertEquals(fileName, 8, error1.getColumnStart());
            assertEquals(fileName, 73, error1.getLineEnd());
            assertEquals(fileName, 25, error1.getColumnEnd());

            final Issue error2 = report.get(35);
            assertEquals(fileName, Severity.ERROR, error2.getSeverity());
            assertEquals(fileName, "preprocessed/src/scenarios.c", error2.getFileName());
            assertEquals(fileName, "Definite runtime error [Errors]", error2.getCategory());
            assertEquals(fileName, 78, error2.getLineStart());
            assertEquals(fileName, 8, error2.getColumnStart());
            assertEquals(fileName, 78, error2.getLineEnd());
            assertEquals(fileName, 16, error2.getColumnEnd());

            final Issue note1 = report.get(36);
            assertEquals(fileName, Severity.WARNING_LOW, note1.getSeverity());
            assertEquals(fileName, "preprocessed/src/scenarios.c", note1.getFileName());
            assertEquals(fileName, "Control flow [Notifications]", note1.getCategory());
            assertEquals(fileName, 48, note1.getLineStart());
            assertEquals(fileName, 1, note1.getColumnStart());
            assertEquals(fileName, 135, note1.getLineEnd());
            assertEquals(fileName, 1, note1.getColumnEnd());

            final Issue note2 = report.get(37);
            assertEquals(fileName, Severity.WARNING_LOW, note2.getSeverity());
            assertEquals(fileName, "preprocessed/src/scenarios.c", note2.getFileName());
            assertEquals(fileName, "Control flow [Notifications]", note2.getCategory());
            assertEquals(fileName, 124, note2.getLineStart());
            assertEquals(fileName, 3, note2.getColumnStart());
            assertEquals(fileName, 126, note2.getLineEnd());
            assertEquals(fileName, 5, note2.getColumnEnd());
        } else if (fileName.compareTo("report-21.04i2.xml") <= 0) {
            assertEquals(fileName, 125, report.size());

            final Issue alarm1 = report.get(0);
            assertEquals(fileName, Severity.WARNING_HIGH, alarm1.getSeverity());
            assertEquals(fileName, "Incompatible object pointer conversion [Failed coding rule checks]", alarm1.getCategory());
            assertEquals(fileName, "preprocessed/src/dhry/Proc1.c", alarm1.getFileName());
            assertEquals(fileName, 77, alarm1.getLineStart());
            assertEquals(fileName, 44, alarm1.getColumnStart());
            assertEquals(fileName, 77, alarm1.getLineEnd());
            assertEquals(fileName, 54, alarm1.getColumnEnd());

            final Issue alarm2 = report.get(119);
            assertEquals(fileName, Severity.WARNING_HIGH, alarm2.getSeverity());
            assertEquals(fileName, "Integer division by zero [Division or modulo by zero]", alarm2.getCategory());
            assertEquals(fileName, "preprocessed/src/dhry/Proc7.c", alarm2.getFileName());
            assertEquals(fileName, 78, alarm2.getLineStart());
            assertEquals(fileName, 12, alarm2.getColumnStart());
            assertEquals(fileName, 78, alarm2.getLineEnd());
            assertEquals(fileName, 22, alarm2.getColumnEnd());

            final Issue error1 = report.get(120);
            assertEquals(fileName, Severity.ERROR, error1.getSeverity());
            if (fileName.compareTo("report-20.04.xml") <= 0) {
                assertEquals(fileName, "Definite runtime error [Errors]", error1.getCategory());
                assertEquals(fileName, "preprocessed/src/scenarios.c", error1.getFileName());
                assertEquals(fileName, 73, error1.getLineStart());
                assertEquals(fileName, 8, error1.getColumnStart());
                assertEquals(fileName, 73, error1.getLineEnd());
                assertEquals(fileName, 25, error1.getColumnEnd());
            } else {
                assertEquals(fileName, "Analysis stopped [Errors]", error1.getCategory());
                assertEquals(fileName, "preprocessed/src/dhry/Proc7.c", error1.getFileName());
                assertEquals(fileName, 78, error1.getLineStart());
                assertEquals(fileName, 3, error1.getColumnStart());
                assertEquals(fileName, 78, error1.getLineEnd());
                assertEquals(fileName, 22, error1.getColumnEnd());
            }

            final Issue error2 = report.get(124);
            assertEquals(fileName, Severity.ERROR, error2.getSeverity());
            if (fileName.compareTo("report-20.04.xml") <= 0) {
                assertEquals(fileName, "Definite runtime error [Errors]", error2.getCategory());
            } else {
                assertEquals(fileName, "Analysis stopped [Errors]", error2.getCategory());
            }
            assertEquals(fileName, "preprocessed/src/dhry/Proc7.c", error2.getFileName());
            assertEquals(fileName, 78, error2.getLineStart());
            assertEquals(fileName, 3, error2.getColumnStart());
            assertEquals(fileName, 78, error2.getLineEnd());
            assertEquals(fileName, 22, error2.getColumnEnd());
        } else {
            assertEquals(fileName, 127, report.size());

            final Issue alarm1 = report.get(0);
            assertEquals(fileName, Severity.WARNING_HIGH, alarm1.getSeverity());
            assertEquals(fileName, "Incompatible object pointer conversion [Failed coding rule checks]", alarm1.getCategory());
            assertEquals(fileName, "preprocessed/src/dhry/Proc1.c", alarm1.getFileName());
            assertEquals(fileName, 77, alarm1.getLineStart());
            assertEquals(fileName, 44, alarm1.getColumnStart());
            assertEquals(fileName, 77, alarm1.getLineEnd());
            assertEquals(fileName, 54, alarm1.getColumnEnd());

            final Issue error1 = report.get(120);
            assertEquals(fileName, Severity.ERROR, error1.getSeverity());
            assertEquals(fileName, "Analysis stopped in critical context [Errors]", error1.getCategory());
            assertEquals(fileName, "preprocessed/src/dhry/Proc7.c", error1.getFileName());
            assertEquals(fileName, 78, error1.getLineStart());
            assertEquals(fileName, 3, error1.getColumnStart());
            assertEquals(fileName, 78, error1.getLineEnd());
            assertEquals(fileName, 22, error1.getColumnEnd());

            final Issue error2 = report.get(124);
            assertEquals(fileName, Severity.ERROR, error2.getSeverity());
            assertEquals(fileName, "Analysis stopped in critical context [Errors]", error2.getCategory());
            assertEquals(fileName, "preprocessed/src/dhry/Proc7.c", error2.getFileName());
            assertEquals(fileName, 78, error2.getLineStart());
            assertEquals(fileName, 3, error2.getColumnStart());
            assertEquals(fileName, 78, error2.getLineEnd());
            assertEquals(fileName, 22, error2.getColumnEnd());
        }
    }
}
