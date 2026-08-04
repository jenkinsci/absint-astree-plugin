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
    void test(Path path) {
        final FileReaderFactory readerFactory = new FileReaderFactory(path);
        final AstreeReportParser parser = new AstreeReportParser();
        assertTrue(parser.accepts(readerFactory));
        testReport(parser.parse(readerFactory));
    }

    static Stream<Arguments> reports() {
        final File dir = new File(AstreeReportParserTest.class.getResource("").getPath());
        return Arrays.stream(Objects.requireNonNull(dir.listFiles()))
                .filter(file -> file.getName().endsWith(".xml"))
                .map(file -> Arguments.of(file.toPath()));
    }

    private void testReport(Report report) {
        final File file = new File(report.getOriginReportFile());
        final String fileName = file.getName();

        // After the switch from preprocessed to original location, Jenkins believes some Issues are duplicates.
        //assertEquals(fileName, 0, report.getDuplicatesSize());

        final Set<String> files = report.getAbsolutePaths();
        if (fileName.compareTo("report-18.10.xml") <= 0) {
            assertEquals(2, files.size());
            assertTrue(files.contains("/tmp/a3c-123456/examples/src/scenarios.c"));
            assertTrue(files.contains("-"));
        } else if (fileName.compareTo("report-21.04i2.xml") <= 0) {
            assertEquals(5, files.size());
            assertTrue(files.contains("/tmp/a3c-123456/examples/src/astree.cfg"));
            assertTrue(files.contains("/tmp/a3c-123456/examples/src/filter.c"));
            assertTrue(files.contains("/tmp/a3c-123456/examples/src/scenarios.c"));
            assertTrue(files.contains("/tmp/a3c-123456/examples/src/dhry/Proc1.c"));
            assertTrue(files.contains("/tmp/a3c-123456/examples/src/dhry/Proc7.c"));
        } else {
            assertEquals(6, files.size());
            assertTrue(files.contains("/tmp/a3c-123456/examples/src/state_machine.c"));
            assertTrue(files.contains("/tmp/a3c-123456/examples/src/astree.cfg"));
            assertTrue(files.contains("/tmp/a3c-123456/examples/src/filter.c"));
            assertTrue(files.contains("/tmp/a3c-123456/examples/src/scenarios.c"));
            assertTrue(files.contains("/tmp/a3c-123456/examples/src/dhry/Proc1.c"));
            assertTrue(files.contains("/tmp/a3c-123456/examples/src/dhry/Proc7.c"));
        }

        final Set<String> categories = report.getCategories();
        if (fileName.compareTo("report-18.10.xml") <= 0) {
            assertEquals(21, categories.size());
        } else if (fileName.compareTo("report-21.04i2.xml") <= 0) {
            assertEquals(11, categories.size());
            assertTrue(categories.contains("Integer division by zero [Division or modulo by zero]"));
            assertTrue(categories.contains("Use of uninitialized variables [Uninitialized variables]"));
            assertTrue(categories.contains("Overflow in conversion (with unpredictable result) [Invalid ranges and overflows]"));
            if (fileName.compareTo("report-20.10.xml") <= 0) {
                assertTrue(categories.contains("Definite runtime error [Errors]"));
            } else {
                assertTrue(categories.contains("Analysis stopped [Errors]"));
            }
            assertTrue(categories.contains("Possible overflow upon dereference [Invalid usage of pointers and arrays]"));
            assertTrue(categories.contains("Assertion failure [Failed or invalid directives]"));
            assertTrue(categories.contains("Incompatible object pointer conversion [Failed coding rule checks]"));
            assertTrue(categories.contains("Infinite loop [Data and control flow alarms]"));
            assertTrue(categories.contains("Control flow anomaly [Data and control flow alarms]"));
            assertTrue(categories.contains("Out-of-bound array access [Invalid usage of pointers and arrays]"));
            assertTrue(categories.contains("Overflow in arithmetic [Invalid ranges and overflows]"));
        } else {
            assertEquals(12, categories.size());
            assertTrue(categories.contains("Integer division by zero [Division or modulo by zero]"));
            assertTrue(categories.contains("Use of uninitialized variables [Uninitialized variables]"));
            assertTrue(categories.contains("Analysis stopped in critical context [Errors]"));
            assertTrue(categories.contains("Unbounded loop [Data and control flow alarms]"));
            assertTrue(categories.contains("Overflow in conversion (with unpredictable result) [Invalid ranges and overflows]"));
            assertTrue(categories.contains("Possible overflow upon dereference [Invalid usage of pointers and arrays]"));
            assertTrue(categories.contains("Assertion failure [Failed or invalid directives]"));
            assertTrue(categories.contains("Incompatible object pointer conversion [Failed coding rule checks]"));
            assertTrue(categories.contains("Infinite loop [Data and control flow alarms]"));
            assertTrue(categories.contains("Control flow anomaly [Data and control flow alarms]"));
            assertTrue(categories.contains("Out-of-bound array access [Invalid usage of pointers and arrays]"));
            if (fileName.compareTo("report-22.10i4.xml") <= 0) {
                assertTrue(categories.contains("Overflow in arithmetic [Invalid ranges and overflows]"));
            } else {
                assertTrue(categories.contains("Overflow in arithmetic (with unpredictable result) [Invalid ranges and overflows]"));
            }
        }

        if (fileName.compareTo("report-18.10.xml") <= 0) {
            assertEquals(36, report.size());

            final Issue alarm1 = report.get(0);
            assertEquals(Severity.WARNING_HIGH, alarm1.getSeverity());
            if (fileName.equals("report-18.04.xml")) {
                assertEquals("Parameter name [Violation of coding rules]", alarm1.getCategory());
            } else {
                assertEquals("Parameter name [Failed coding rule checks]", alarm1.getCategory());
            }
            assertEquals("/tmp/a3c-123456/examples/src/scenarios.c", alarm1.getFileName());
            assertEquals(26, alarm1.getLineStart());
            assertEquals(0, alarm1.getColumnStart());
            assertEquals(26, alarm1.getLineEnd());
            assertEquals(0, alarm1.getColumnEnd());
            assertEquals("ALARM (R): check parameter-name failed (violates M2012.8.2-required)", alarm1.getMessage());
            if (fileName.equals("report-18.04.xml")) {
                assertEquals("n/a", alarm1.getReference());
            } else {
                assertEquals("n/A", alarm1.getReference());
            }
            assertEquals("", alarm1.getDescription());

            final Issue alarm2 = report.get(35);
            assertEquals(Severity.WARNING_HIGH, alarm2.getSeverity());
            if (fileName.equals("report-18.04.xml")) {
                assertEquals("Unreachable code [Violation of coding rules]", alarm2.getCategory());
            } else {
                assertEquals("Unreachable code [Failed coding rule checks]", alarm2.getCategory());
            }
            assertEquals("/tmp/a3c-123456/examples/src/scenarios.c", alarm2.getFileName());
            assertEquals(140, alarm2.getLineStart());
            assertEquals(0, alarm2.getColumnStart());
            assertEquals(140, alarm2.getLineEnd());
            assertEquals(0, alarm2.getColumnEnd());
            assertEquals("ALARM (R): check unreachable-code failed (violates M2012.2.1-required)", alarm2.getMessage());
            if (fileName.equals("report-18.04.xml")) {
                assertEquals("n/a", alarm2.getReference());
            } else {
                assertEquals("n/A", alarm2.getReference());
            }
            assertEquals("", alarm2.getDescription());

            final Issue error1 = report.get(20);
            assertEquals(Severity.ERROR, error1.getSeverity());
            assertEquals("/tmp/a3c-123456/examples/src/scenarios.c", error1.getFileName());
            assertEquals("Definite runtime error [Errors]", error1.getCategory());
            assertEquals(79, error1.getLineStart());
            assertEquals(0, error1.getColumnStart());
            assertEquals(79, error1.getLineEnd());
            assertEquals(0, error1.getColumnEnd());
            assertEquals("ERROR: Definite runtime error during assignment in this context. Analysis stopped for this context.", error1.getMessage());
            if (fileName.equals("report-18.04.xml")) {
                assertEquals("n/a", error1.getReference());
            } else {
                assertEquals("n/A", error1.getReference());
            }
            assertEquals("<p>Context:</p><pre>l32#call#main@48,l34#loop@72=11/12</pre>", error1.getDescription());

            final Issue error2 = report.get(23);
            assertEquals(Severity.ERROR, error2.getSeverity());
            assertEquals("/tmp/a3c-123456/examples/src/scenarios.c", error2.getFileName());
            assertEquals("Definite runtime error [Errors]", error2.getCategory());
            assertEquals(84, error2.getLineStart());
            assertEquals(0, error2.getColumnStart());
            assertEquals(84, error2.getLineEnd());
            assertEquals(0, error2.getColumnEnd());
            assertEquals("ERROR: Definite runtime error during assignment in this context. Analysis stopped for this context.", error2.getMessage());
            if (fileName.equals("report-18.04.xml")) {
                assertEquals("n/a", error2.getReference());
            } else {
                assertEquals("n/A", error2.getReference());
            }
            assertEquals("<p>Context:</p><pre>l32#call#main@48,l37#loop@77=11/12</pre>", error2.getDescription());

            final Issue note1 = report.get(28);
            assertEquals(Severity.WARNING_LOW, note1.getSeverity());
            assertEquals("/tmp/a3c-123456/examples/src/scenarios.c", note1.getFileName());
            assertEquals("Control flow [Notifications]", note1.getCategory());
            assertEquals(54, note1.getLineStart());
            assertEquals(0, note1.getColumnStart());
            assertEquals(141, note1.getLineEnd());
            assertEquals(0, note1.getColumnEnd());
            assertEquals("NOTE: Analyzed entry-point main never returns.", note1.getMessage());
            if (fileName.equals("report-18.04.xml")) {
                assertEquals("n/a", note1.getReference());
            } else {
                assertEquals("n/A", note1.getReference());
            }
            assertEquals("", note1.getDescription());

            final Issue note2 = report.get(33);
            assertEquals(Severity.WARNING_LOW, note2.getSeverity());
            assertEquals("/tmp/a3c-123456/examples/src/scenarios.c", note2.getFileName());
            assertEquals("Control flow [Notifications]", note2.getCategory());
            assertEquals(130, note2.getLineStart());
            assertEquals(0, note2.getColumnStart());
            assertEquals(132, note2.getLineEnd());
            assertEquals(0, note2.getColumnEnd());
            assertEquals("NOTE: Loop may be unbounded", note2.getMessage());
            if (fileName.equals("report-18.04.xml")) {
                assertEquals("n/a", note2.getReference());
            } else {
                assertEquals("n/A", note2.getReference());
            }
            assertEquals("", note2.getDescription());
        } else if (fileName.compareTo("report-21.04i2.xml") <= 0) {
            assertEquals(21, report.size());

            final Issue alarm1 = report.get(0);
            assertEquals(Severity.WARNING_HIGH, alarm1.getSeverity());
            assertEquals("Incompatible object pointer conversion [Failed coding rule checks]", alarm1.getCategory());
            assertEquals("/tmp/a3c-123456/examples/src/dhry/Proc1.c", alarm1.getFileName());
            assertEquals(7, alarm1.getLineStart());
            assertEquals(0, alarm1.getColumnStart());
            assertEquals(7, alarm1.getLineEnd());
            assertEquals(0, alarm1.getColumnEnd());
            if (fileName.compareTo("report-19.10.xml") <= 0) {
                assertEquals("[ conversion between two incompatible pointer types: from &lt;RecordType*&gt; (aka &lt;struct Record*&gt;) to &lt;char*&gt;<br>ALARM (R): check incompatible-object-pointer-conversion failed (violates A.1.11)", alarm1.getMessage());
            } else if (fileName.compareTo("report-20.04.xml") <= 0) {
                assertEquals("conversion between two incompatible pointer types: from &lt;RecordType*&gt; (aka &lt;struct Record*&gt;) to &lt;char*&gt;<br>ALARM (R): check incompatible-object-pointer-conversion failed (violates A.1.11)", alarm1.getMessage());
            } else if (fileName.compareTo("report-21.04.xml") <= 0) {
                assertEquals("conversion between two incompatible pointer types: from &lt;RecordType*&gt; (aka &lt;struct Record*&gt;) to &lt;char*&gt;<br>ALARM (R) check_incompatible_object_pointer_conversion: check failed (violates A.1.11)", alarm1.getMessage());
            } else {
                assertEquals("conversion between two incompatible pointer types: from &lt;RecordType *&gt; (aka &lt;struct Record *&gt;) to &lt;char *&gt;<br>ALARM (R) check_incompatible_object_pointer_conversion: check failed (violates A.1.11)", alarm1.getMessage());
            }
            assertEquals("n/A", alarm1.getReference());
            assertEquals("<p>Code:</p><pre>" +
                "memcpy_x(&amp;((*(PtrParIn-&gt;PtrComp))), &amp;(*PtrGlb), sizeof((*(PtrParIn-&gt;PtrComp))));\n" +
                "                                    ~~~~~~~~~~</pre>",
                alarm1.getDescription());

            final Issue alarm2 = report.get(19);
            assertEquals(Severity.WARNING_HIGH, alarm2.getSeverity());
            assertEquals("Integer division by zero [Division or modulo by zero]", alarm2.getCategory());
            assertEquals("/tmp/a3c-123456/examples/src/dhry/Proc7.c", alarm2.getFileName());
            assertEquals(8, alarm2.getLineStart());
            assertEquals(0, alarm2.getColumnStart());
            assertEquals(8, alarm2.getLineEnd());
            assertEquals(0, alarm2.getColumnEnd());
            if (fileName.compareTo("report-20.04.xml") <= 0) {
                assertEquals("ALARM (A): integer division by zero {0}", alarm2.getMessage());
            } else {
                assertEquals("ALARM (A) int_division_by_zero: divisor in {0}", alarm2.getMessage());
            }
            assertEquals("n/A", alarm2.getReference());
            assertTrue(alarm2.getDescription().startsWith("<p>Code:</p><pre>IntLoc = IntParI1/0;\n         ~~~~~~~~~~</pre><p>Context:</p><pre>l"));
            assertTrue(alarm2.getDescription().endsWith("#call#Proc7</pre>"));

            final Issue error1 = report.get(8);
            assertEquals(Severity.ERROR, error1.getSeverity());
            if (fileName.compareTo("report-20.10.xml") <= 0) {
                assertEquals("Definite runtime error [Errors]", error1.getCategory());
                if (fileName.compareTo("report-20.04.xml") <= 0) {
                    assertEquals("ERROR: Definite runtime error during assignment in this context. Analysis stopped for this context.", error1.getMessage());
                } else {
                    assertEquals("ERROR: Definite runtime error during assignment in this context. Analysis stopped for this context", error1.getMessage());
                }
            } else {
                assertEquals("Analysis stopped [Errors]", error1.getCategory());
                assertEquals("ERROR analysis_stopped: Definite runtime error during assignment in this context. Analysis stopped for this context", error1.getMessage());
            }
            assertEquals("/tmp/a3c-123456/examples/src/scenarios.c", error1.getFileName());
            assertEquals(76, error1.getLineStart());
            assertEquals(0, error1.getColumnStart());
            assertEquals(76, error1.getLineEnd());
            assertEquals(0, error1.getColumnEnd());
            assertEquals("n/A", error1.getReference());
            if (fileName.compareTo("report-20.04.xml") <= 0) {
                assertTrue(error1.getDescription().startsWith("<p>Context:</p><pre>l"));
            } else {
                assertTrue(error1.getDescription().startsWith("<p>Code:</p><pre>ArrayBlock[i] = i;\n~~~~~~~~~~~~~~~~~</pre><p>Context:</p><pre>l"));
            }
            assertTrue(error1.getDescription().endsWith("</pre>"));

            final Issue error2 = report.get(20);
            assertEquals(Severity.ERROR, error2.getSeverity());
            if (fileName.compareTo("report-20.10.xml") <= 0) {
                assertEquals("Definite runtime error [Errors]", error2.getCategory());
            } else {
                assertEquals("Analysis stopped [Errors]", error2.getCategory());
            }
            assertEquals("/tmp/a3c-123456/examples/src/dhry/Proc7.c", error2.getFileName());
            assertEquals(8, error2.getLineStart());
            assertEquals(0, error2.getColumnStart());
            assertEquals(8, error2.getLineEnd());
            assertEquals(0, error2.getColumnEnd());
            if (fileName.compareTo("report-20.04.xml") <= 0) {
                assertEquals("ERROR: Definite runtime error during assignment in this context. Analysis stopped for this context.", error2.getMessage());
            } else if (fileName.compareTo("report-20.10.xml") <= 0) {
                assertEquals("ERROR: Definite runtime error during assignment in this context. Analysis stopped for this context", error2.getMessage());
            } else {
                assertEquals("ERROR analysis_stopped: Definite runtime error during assignment in this context. Analysis stopped for this context", error2.getMessage());
            }
            assertEquals("n/A", error2.getReference());
            if (fileName.compareTo("report-20.04.xml") <= 0) {
                assertTrue(error2.getDescription().startsWith("<p>Context:</p><pre>l"));
            } else {
                assertTrue(error2.getDescription().startsWith("<p>Code:</p><pre>IntLoc = IntParI1/0;\n~~~~~~~~~~~~~~~~~~~</pre><p>Context:</p><pre>l"));
            }
            assertTrue(error2.getDescription().endsWith("</pre>"));
        } else {
            assertEquals(23, report.size());

            final Issue alarm1 = report.get(0);
            assertEquals(Severity.WARNING_HIGH, alarm1.getSeverity());
            assertEquals("Incompatible object pointer conversion [Failed coding rule checks]", alarm1.getCategory());
            assertEquals("/tmp/a3c-123456/examples/src/dhry/Proc1.c", alarm1.getFileName());
            assertEquals(7, alarm1.getLineStart());
            assertEquals(0, alarm1.getColumnStart());
            assertEquals(7, alarm1.getLineEnd());
            assertEquals(0, alarm1.getColumnEnd());
            assertEquals("conversion between two incompatible pointer types: from &lt;RecordType *&gt; (aka &lt;struct Record *&gt;) to &lt;char *&gt;<br>ALARM (R) check_incompatible_object_pointer_conversion: check failed (violates A.1.11)", alarm1.getMessage());
            assertEquals("n/A", alarm1.getReference());
            assertEquals("<p>Code:</p><pre>" +
                "memcpy_x(&amp;((*(PtrParIn-&gt;PtrComp))), &amp;(*PtrGlb), sizeof((*(PtrParIn-&gt;PtrComp))));\n" +
                "                                    ~~~~~~~~~~</pre>",
                alarm1.getDescription());

            final Issue error1 = report.get(20);
            assertEquals(Severity.ERROR, error1.getSeverity());
            assertEquals("Analysis stopped in critical context [Errors]", error1.getCategory());
            assertEquals("/tmp/a3c-123456/examples/src/dhry/Proc7.c", error1.getFileName());
            assertEquals(8, error1.getLineStart());
            assertEquals(0, error1.getColumnStart());
            assertEquals(8, error1.getLineEnd());
            assertEquals(0, error1.getColumnEnd());
            assertEquals("ERROR analysis_stopped: Definite runtime error during assignment in this context. Analysis stopped for this context", error1.getMessage());
            assertEquals("n/A", error1.getReference());
            assertTrue(error1.getDescription().startsWith("<p>Code:</p><pre>IntLoc = IntParI1/0;\n~~~~~~~~~~~~~~~~~~~</pre><p>Context:</p><pre>l"));
            assertTrue(error1.getDescription().endsWith("#call#Proc7</pre>"));
        }
    }
}
