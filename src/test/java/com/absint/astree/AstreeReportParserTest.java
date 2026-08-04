package com.absint.astree;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import edu.hm.hafner.analysis.FileReaderFactory;
import edu.hm.hafner.analysis.Issue;
import edu.hm.hafner.analysis.Report;
import edu.hm.hafner.analysis.Severity;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Tests for {@link AstreeReportParser}.
 */
class AstreeReportParserTest {

    @ParameterizedTest(name = "{0}")
    @MethodSource("reports")
    void test(String fileName, Path path) {
        final FileReaderFactory readerFactory = new FileReaderFactory(path);
        final AstreeReportParser parser = new AstreeReportParser();
        assertTrue(parser.accepts(readerFactory));
        testReport(parser.parse(readerFactory));
    }

    static Stream<Arguments> reports() {
        final File dir = new File(AstreeReportParserTest.class.getResource("").getPath());
        return Arrays.stream(Objects.requireNonNull(dir.listFiles()))
                .filter(file -> file.getName().endsWith(".xml"))
                .map(file -> Arguments.of(file.getName(), file.toPath()));
    }

    private void testReport(Report report) {
        final File file = new File(report.getOriginReportFile());
        final String fileName = file.getName();

        // After the switch from preprocessed to original location, Jenkins believes some Issues are duplicates.
        //assertEquals(fileName, 0, report.getDuplicatesSize());

        final Set<String> files = report.getAbsolutePaths();
        if (fileName.compareTo("report-18.10.xml") <= 0) {
            assertEquals(2, files.size(), fileName);
            assertTrue(files.contains("/tmp/a3c-123456/examples/src/scenarios.c"), fileName);
            assertTrue(files.contains("-"), fileName);
        } else if (fileName.compareTo("report-21.04i2.xml") <= 0) {
            assertEquals(5, files.size(), fileName);
            assertTrue(files.contains("/tmp/a3c-123456/examples/src/astree.cfg"), fileName);
            assertTrue(files.contains("/tmp/a3c-123456/examples/src/filter.c"), fileName);
            assertTrue(files.contains("/tmp/a3c-123456/examples/src/scenarios.c"), fileName);
            assertTrue(files.contains("/tmp/a3c-123456/examples/src/dhry/Proc1.c"), fileName);
            assertTrue(files.contains("/tmp/a3c-123456/examples/src/dhry/Proc7.c"), fileName);
        } else {
            assertEquals(6, files.size(), fileName);
            assertTrue(files.contains("/tmp/a3c-123456/examples/src/state_machine.c"), fileName);
            assertTrue(files.contains("/tmp/a3c-123456/examples/src/astree.cfg"), fileName);
            assertTrue(files.contains("/tmp/a3c-123456/examples/src/filter.c"), fileName);
            assertTrue(files.contains("/tmp/a3c-123456/examples/src/scenarios.c"), fileName);
            assertTrue(files.contains("/tmp/a3c-123456/examples/src/dhry/Proc1.c"), fileName);
            assertTrue(files.contains("/tmp/a3c-123456/examples/src/dhry/Proc7.c"), fileName);
        }

        final Set<String> categories = report.getCategories();
        if (fileName.compareTo("report-18.10.xml") <= 0) {
            assertEquals(21, categories.size(), fileName);
        } else if (fileName.compareTo("report-21.04i2.xml") <= 0) {
            assertEquals(11, categories.size(), fileName);
            assertTrue(categories.contains("Integer division by zero [Division or modulo by zero]"), fileName);
            assertTrue(categories.contains("Use of uninitialized variables [Uninitialized variables]"), fileName);
            assertTrue(categories.contains("Overflow in conversion (with unpredictable result) [Invalid ranges and overflows]"), fileName);
            if (fileName.compareTo("report-20.10.xml") <= 0) {
                assertTrue(categories.contains("Definite runtime error [Errors]"), fileName);
            } else {
                assertTrue(categories.contains("Analysis stopped [Errors]"), fileName);
            }
            assertTrue(categories.contains("Possible overflow upon dereference [Invalid usage of pointers and arrays]"), fileName);
            assertTrue(categories.contains("Assertion failure [Failed or invalid directives]"), fileName);
            assertTrue(categories.contains("Incompatible object pointer conversion [Failed coding rule checks]"), fileName);
            assertTrue(categories.contains("Infinite loop [Data and control flow alarms]"), fileName);
            assertTrue(categories.contains("Control flow anomaly [Data and control flow alarms]"), fileName);
            assertTrue(categories.contains("Out-of-bound array access [Invalid usage of pointers and arrays]"), fileName);
            assertTrue(categories.contains("Overflow in arithmetic [Invalid ranges and overflows]"), fileName);
        } else {
            assertEquals(12, categories.size(), fileName);
            assertTrue(categories.contains("Integer division by zero [Division or modulo by zero]"), fileName);
            assertTrue(categories.contains("Use of uninitialized variables [Uninitialized variables]"), fileName);
            assertTrue(categories.contains("Analysis stopped in critical context [Errors]"), fileName);
            assertTrue(categories.contains("Unbounded loop [Data and control flow alarms]"), fileName);
            assertTrue(categories.contains("Overflow in conversion (with unpredictable result) [Invalid ranges and overflows]"), fileName);
            assertTrue(categories.contains("Possible overflow upon dereference [Invalid usage of pointers and arrays]"), fileName);
            assertTrue(categories.contains("Assertion failure [Failed or invalid directives]"), fileName);
            assertTrue(categories.contains("Incompatible object pointer conversion [Failed coding rule checks]"), fileName);
            assertTrue(categories.contains("Infinite loop [Data and control flow alarms]"), fileName);
            assertTrue(categories.contains("Control flow anomaly [Data and control flow alarms]"), fileName);
            assertTrue(categories.contains("Out-of-bound array access [Invalid usage of pointers and arrays]"), fileName);
            if (fileName.compareTo("report-22.10i4.xml") <= 0) {
                assertTrue(categories.contains("Overflow in arithmetic [Invalid ranges and overflows]"), fileName);
            } else {
                assertTrue(categories.contains("Overflow in arithmetic (with unpredictable result) [Invalid ranges and overflows]"), fileName);
            }
        }

        if (fileName.compareTo("report-18.10.xml") <= 0) {
            assertEquals(36, report.size(), fileName);

            final Issue alarm1 = report.get(0);
            assertEquals(Severity.WARNING_HIGH, alarm1.getSeverity(), fileName);
            if (fileName.equals("report-18.04.xml")) {
                assertEquals("Parameter name [Violation of coding rules]", alarm1.getCategory(), fileName);
            } else {
                assertEquals("Parameter name [Failed coding rule checks]", alarm1.getCategory(), fileName);
            }
            assertEquals("/tmp/a3c-123456/examples/src/scenarios.c", alarm1.getFileName(), fileName);
            assertEquals(26, alarm1.getLineStart(), fileName);
            assertEquals(0, alarm1.getColumnStart(), fileName);
            assertEquals(26, alarm1.getLineEnd(), fileName);
            assertEquals(0, alarm1.getColumnEnd(), fileName);
            assertEquals("ALARM (R): check parameter-name failed (violates M2012.8.2-required)", alarm1.getMessage(), fileName);
            if (fileName.equals("report-18.04.xml")) {
                assertEquals("n/a", alarm1.getReference(), fileName);
            } else {
                assertEquals("n/A", alarm1.getReference(), fileName);
            }
            assertEquals("", alarm1.getDescription(), fileName);

            final Issue alarm2 = report.get(35);
            assertEquals(Severity.WARNING_HIGH, alarm2.getSeverity(), fileName);
            if (fileName.equals("report-18.04.xml")) {
                assertEquals("Unreachable code [Violation of coding rules]", alarm2.getCategory(), fileName);
            } else {
                assertEquals("Unreachable code [Failed coding rule checks]", alarm2.getCategory(), fileName);
            }
            assertEquals("/tmp/a3c-123456/examples/src/scenarios.c", alarm2.getFileName(), fileName);
            assertEquals(140, alarm2.getLineStart(), fileName);
            assertEquals(0, alarm2.getColumnStart(), fileName);
            assertEquals(140, alarm2.getLineEnd(), fileName);
            assertEquals(0, alarm2.getColumnEnd(), fileName);
            assertEquals("ALARM (R): check unreachable-code failed (violates M2012.2.1-required)", alarm2.getMessage(), fileName);
            if (fileName.equals("report-18.04.xml")) {
                assertEquals("n/a", alarm2.getReference(), fileName);
            } else {
                assertEquals("n/A", alarm2.getReference(), fileName);
            }
            assertEquals("", alarm2.getDescription(), fileName);

            final Issue error1 = report.get(20);
            assertEquals(Severity.ERROR, error1.getSeverity(), fileName);
            assertEquals("/tmp/a3c-123456/examples/src/scenarios.c", error1.getFileName(), fileName);
            assertEquals("Definite runtime error [Errors]", error1.getCategory(), fileName);
            assertEquals(79, error1.getLineStart(), fileName);
            assertEquals(0, error1.getColumnStart(), fileName);
            assertEquals(79, error1.getLineEnd(), fileName);
            assertEquals(0, error1.getColumnEnd(), fileName);
            assertEquals("ERROR: Definite runtime error during assignment in this context. Analysis stopped for this context.", error1.getMessage(), fileName);
            if (fileName.equals("report-18.04.xml")) {
                assertEquals("n/a", error1.getReference(), fileName);
            } else {
                assertEquals("n/A", error1.getReference(), fileName);
            }
            assertEquals("<p>Context:</p><pre>l32#call#main@48,l34#loop@72=11/12</pre>", error1.getDescription(), fileName);

            final Issue error2 = report.get(23);
            assertEquals(Severity.ERROR, error2.getSeverity(), fileName);
            assertEquals("/tmp/a3c-123456/examples/src/scenarios.c", error2.getFileName(), fileName);
            assertEquals("Definite runtime error [Errors]", error2.getCategory(), fileName);
            assertEquals(84, error2.getLineStart(), fileName);
            assertEquals(0, error2.getColumnStart(), fileName);
            assertEquals(84, error2.getLineEnd(), fileName);
            assertEquals(0, error2.getColumnEnd(), fileName);
            assertEquals("ERROR: Definite runtime error during assignment in this context. Analysis stopped for this context.", error2.getMessage(), fileName);
            if (fileName.equals("report-18.04.xml")) {
                assertEquals("n/a", error2.getReference(), fileName);
            } else {
                assertEquals("n/A", error2.getReference(), fileName);
            }
            assertEquals("<p>Context:</p><pre>l32#call#main@48,l37#loop@77=11/12</pre>", error2.getDescription(), fileName);

            final Issue note1 = report.get(28);
            assertEquals(Severity.WARNING_LOW, note1.getSeverity(), fileName);
            assertEquals("/tmp/a3c-123456/examples/src/scenarios.c", note1.getFileName(), fileName);
            assertEquals("Control flow [Notifications]", note1.getCategory(), fileName);
            assertEquals(54, note1.getLineStart(), fileName);
            assertEquals(0, note1.getColumnStart(), fileName);
            assertEquals(141, note1.getLineEnd(), fileName);
            assertEquals(0, note1.getColumnEnd(), fileName);
            assertEquals("NOTE: Analyzed entry-point main never returns.", note1.getMessage(), fileName);
            if (fileName.equals("report-18.04.xml")) {
                assertEquals("n/a", note1.getReference(), fileName);
            } else {
                assertEquals("n/A", note1.getReference(), fileName);
            }
            assertEquals("", note1.getDescription(), fileName);

            final Issue note2 = report.get(33);
            assertEquals(Severity.WARNING_LOW, note2.getSeverity(), fileName);
            assertEquals("/tmp/a3c-123456/examples/src/scenarios.c", note2.getFileName(), fileName);
            assertEquals("Control flow [Notifications]", note2.getCategory(), fileName);
            assertEquals(130, note2.getLineStart(), fileName);
            assertEquals(0, note2.getColumnStart(), fileName);
            assertEquals(132, note2.getLineEnd(), fileName);
            assertEquals(0, note2.getColumnEnd(), fileName);
            assertEquals("NOTE: Loop may be unbounded", note2.getMessage(), fileName);
            if (fileName.equals("report-18.04.xml")) {
                assertEquals("n/a", note2.getReference(), fileName);
            } else {
                assertEquals("n/A", note2.getReference(), fileName);
            }
            assertEquals("", note2.getDescription(), fileName);
        } else if (fileName.compareTo("report-21.04i2.xml") <= 0) {
            assertEquals(21, report.size(), fileName);

            final Issue alarm1 = report.get(0);
            assertEquals(Severity.WARNING_HIGH, alarm1.getSeverity(), fileName);
            assertEquals("Incompatible object pointer conversion [Failed coding rule checks]", alarm1.getCategory(), fileName);
            assertEquals("/tmp/a3c-123456/examples/src/dhry/Proc1.c", alarm1.getFileName(), fileName);
            assertEquals(7, alarm1.getLineStart(), fileName);
            assertEquals(0, alarm1.getColumnStart(), fileName);
            assertEquals(7, alarm1.getLineEnd(), fileName);
            assertEquals(0, alarm1.getColumnEnd(), fileName);
            if (fileName.compareTo("report-19.10.xml") <= 0) {
                assertEquals("[ conversion between two incompatible pointer types: from &lt;RecordType*&gt; (aka &lt;struct Record*&gt;) to &lt;char*&gt;<br>ALARM (R): check incompatible-object-pointer-conversion failed (violates A.1.11)", alarm1.getMessage(), fileName);
            } else if (fileName.compareTo("report-20.04.xml") <= 0) {
                assertEquals("conversion between two incompatible pointer types: from &lt;RecordType*&gt; (aka &lt;struct Record*&gt;) to &lt;char*&gt;<br>ALARM (R): check incompatible-object-pointer-conversion failed (violates A.1.11)", alarm1.getMessage(), fileName);
            } else if (fileName.compareTo("report-21.04.xml") <= 0) {
                assertEquals("conversion between two incompatible pointer types: from &lt;RecordType*&gt; (aka &lt;struct Record*&gt;) to &lt;char*&gt;<br>ALARM (R) check_incompatible_object_pointer_conversion: check failed (violates A.1.11)", alarm1.getMessage(), fileName);
            } else {
                assertEquals("conversion between two incompatible pointer types: from &lt;RecordType *&gt; (aka &lt;struct Record *&gt;) to &lt;char *&gt;<br>ALARM (R) check_incompatible_object_pointer_conversion: check failed (violates A.1.11)", alarm1.getMessage(), fileName);
            }
            assertEquals("n/A", alarm1.getReference(), fileName);
            assertEquals("<p>Code:</p><pre>" +
                "memcpy_x(&amp;((*(PtrParIn-&gt;PtrComp))), &amp;(*PtrGlb), sizeof((*(PtrParIn-&gt;PtrComp))));\n" +
                "                                    ~~~~~~~~~~</pre>",
                alarm1.getDescription(),
                fileName);

            final Issue alarm2 = report.get(19);
            assertEquals(Severity.WARNING_HIGH, alarm2.getSeverity(), fileName);
            assertEquals("Integer division by zero [Division or modulo by zero]", alarm2.getCategory(), fileName);
            assertEquals("/tmp/a3c-123456/examples/src/dhry/Proc7.c", alarm2.getFileName(), fileName);
            assertEquals(8, alarm2.getLineStart(), fileName);
            assertEquals(0, alarm2.getColumnStart(), fileName);
            assertEquals(8, alarm2.getLineEnd(), fileName);
            assertEquals(0, alarm2.getColumnEnd(), fileName);
            if (fileName.compareTo("report-20.04.xml") <= 0) {
                assertEquals("ALARM (A): integer division by zero {0}", alarm2.getMessage(), fileName);
            } else {
                assertEquals("ALARM (A) int_division_by_zero: divisor in {0}", alarm2.getMessage(), fileName);
            }
            assertEquals("n/A", alarm2.getReference(), fileName);
            assertTrue(alarm2.getDescription().startsWith("<p>Code:</p><pre>IntLoc = IntParI1/0;\n         ~~~~~~~~~~</pre><p>Context:</p><pre>l"), fileName);
            assertTrue(alarm2.getDescription().endsWith("#call#Proc7</pre>"), fileName);

            final Issue error1 = report.get(8);
            assertEquals(Severity.ERROR, error1.getSeverity(), fileName);
            if (fileName.compareTo("report-20.10.xml") <= 0) {
                assertEquals("Definite runtime error [Errors]", error1.getCategory(), fileName);
                if (fileName.compareTo("report-20.04.xml") <= 0) {
                    assertEquals("ERROR: Definite runtime error during assignment in this context. Analysis stopped for this context.", error1.getMessage(), fileName);
                } else {
                    assertEquals("ERROR: Definite runtime error during assignment in this context. Analysis stopped for this context", error1.getMessage(), fileName);
                }
            } else {
                assertEquals("Analysis stopped [Errors]", error1.getCategory(), fileName);
                assertEquals("ERROR analysis_stopped: Definite runtime error during assignment in this context. Analysis stopped for this context", error1.getMessage(), fileName);
            }
            assertEquals("/tmp/a3c-123456/examples/src/scenarios.c", error1.getFileName(), fileName);
            assertEquals(76, error1.getLineStart(), fileName);
            assertEquals(0, error1.getColumnStart(), fileName);
            assertEquals(76, error1.getLineEnd(), fileName);
            assertEquals(0, error1.getColumnEnd(), fileName);
            assertEquals("n/A", error1.getReference(), fileName);
            if (fileName.compareTo("report-20.04.xml") <= 0) {
                assertTrue(error1.getDescription().startsWith("<p>Context:</p><pre>l"), fileName);
            } else {
                assertTrue(error1.getDescription().startsWith("<p>Code:</p><pre>ArrayBlock[i] = i;\n~~~~~~~~~~~~~~~~~</pre><p>Context:</p><pre>l"), fileName);
            }
            assertTrue(error1.getDescription().endsWith("</pre>"), fileName);

            final Issue error2 = report.get(20);
            assertEquals(Severity.ERROR, error2.getSeverity(), fileName);
            if (fileName.compareTo("report-20.10.xml") <= 0) {
                assertEquals("Definite runtime error [Errors]", error2.getCategory(), fileName);
            } else {
                assertEquals("Analysis stopped [Errors]", error2.getCategory(), fileName);
            }
            assertEquals("/tmp/a3c-123456/examples/src/dhry/Proc7.c", error2.getFileName(), fileName);
            assertEquals(8, error2.getLineStart(), fileName);
            assertEquals(0, error2.getColumnStart(), fileName);
            assertEquals(8, error2.getLineEnd(), fileName);
            assertEquals(0, error2.getColumnEnd(), fileName);
            if (fileName.compareTo("report-20.04.xml") <= 0) {
                assertEquals("ERROR: Definite runtime error during assignment in this context. Analysis stopped for this context.", error2.getMessage(), fileName);
            } else if (fileName.compareTo("report-20.10.xml") <= 0) {
                assertEquals("ERROR: Definite runtime error during assignment in this context. Analysis stopped for this context", error2.getMessage(), fileName);
            } else {
                assertEquals("ERROR analysis_stopped: Definite runtime error during assignment in this context. Analysis stopped for this context", error2.getMessage(), fileName);
            }
            assertEquals("n/A", error2.getReference(), fileName);
            if (fileName.compareTo("report-20.04.xml") <= 0) {
                assertTrue(error2.getDescription().startsWith("<p>Context:</p><pre>l"), fileName);
            } else {
                assertTrue(error2.getDescription().startsWith("<p>Code:</p><pre>IntLoc = IntParI1/0;\n~~~~~~~~~~~~~~~~~~~</pre><p>Context:</p><pre>l"), fileName);
            }
            assertTrue(error2.getDescription().endsWith("</pre>"), fileName);
        } else {
            assertEquals(23, report.size(), fileName);

            final Issue alarm1 = report.get(0);
            assertEquals(Severity.WARNING_HIGH, alarm1.getSeverity(), fileName);
            assertEquals("Incompatible object pointer conversion [Failed coding rule checks]", alarm1.getCategory(), fileName);
            assertEquals("/tmp/a3c-123456/examples/src/dhry/Proc1.c", alarm1.getFileName(), fileName);
            assertEquals(7, alarm1.getLineStart(), fileName);
            assertEquals(0, alarm1.getColumnStart(), fileName);
            assertEquals(7, alarm1.getLineEnd(), fileName);
            assertEquals(0, alarm1.getColumnEnd(), fileName);
            assertEquals("conversion between two incompatible pointer types: from &lt;RecordType *&gt; (aka &lt;struct Record *&gt;) to &lt;char *&gt;<br>ALARM (R) check_incompatible_object_pointer_conversion: check failed (violates A.1.11)", alarm1.getMessage(), fileName);
            assertEquals("n/A", alarm1.getReference(), fileName);
            assertEquals("<p>Code:</p><pre>" +
                "memcpy_x(&amp;((*(PtrParIn-&gt;PtrComp))), &amp;(*PtrGlb), sizeof((*(PtrParIn-&gt;PtrComp))));\n" +
                "                                    ~~~~~~~~~~</pre>",
                alarm1.getDescription(),
                fileName);

            final Issue error1 = report.get(20);
            assertEquals(Severity.ERROR, error1.getSeverity(), fileName);
            assertEquals("Analysis stopped in critical context [Errors]", error1.getCategory(), fileName);
            assertEquals("/tmp/a3c-123456/examples/src/dhry/Proc7.c", error1.getFileName(), fileName);
            assertEquals(8, error1.getLineStart(), fileName);
            assertEquals(0, error1.getColumnStart(), fileName);
            assertEquals(8, error1.getLineEnd(), fileName);
            assertEquals(0, error1.getColumnEnd(), fileName);
            assertEquals("ERROR analysis_stopped: Definite runtime error during assignment in this context. Analysis stopped for this context", error1.getMessage(), fileName);
            assertEquals("n/A", error1.getReference(), fileName);
            assertTrue(error1.getDescription().startsWith("<p>Code:</p><pre>IntLoc = IntParI1/0;\n~~~~~~~~~~~~~~~~~~~</pre><p>Context:</p><pre>l"), fileName);
            assertTrue(error1.getDescription().endsWith("#call#Proc7</pre>"), fileName);
        }
    }
}
