/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.codenarc.report

import static org.codenarc.test.TestUtil.captureSystemOut
import static org.codenarc.test.TestUtil.shouldFailWithMessageContaining
import static org.junit.Assert.assertEquals

import org.codenarc.AnalysisContext
import org.codenarc.results.DirectoryResults
import org.codenarc.results.FileResults
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Abstract superclass for TestReportWriter and subclass tests
 *
 * @author Chris Mair
 * @author Hamlet D'Arcy
 */
abstract class AbstractTextReportWriterTestCase extends AbstractReportWriterTestCase {

    protected static final String NEW_REPORT_FILE = 'target/NewTextReport.txt'

    protected reportWriter
    protected analysisContext
    protected results, srcMainDaoDirResults
    protected stringWriter

    //------------------------------------------------------------------------------------
    // Abstract declarations
    //------------------------------------------------------------------------------------

    protected abstract TextReportWriter createReportWriter()
    protected abstract String getReportTextMaxPriority()
    protected abstract String getReportText()

    //------------------------------------------------------------------------------------
    // Tests
    //------------------------------------------------------------------------------------

    @Test
    void testWriteReport_Writer() {
        reportWriter.writeReport(stringWriter, analysisContext, results)
        def reportText = stringWriter.toString()
        assertReportText(reportText, getReportText())
    }

    @Test
    void testWriteReport_MaxPriority() {
        reportWriter.maxPriority = 1
        reportWriter.writeReport(stringWriter, analysisContext, results)
        def reportText = stringWriter.toString()
        assertReportText(reportText, getReportTextMaxPriority())
    }

    @Test
    void testWriteReport_WritesToDefaultReportFile() {
        reportWriter.writeReport(analysisContext, results)
        def reportFile = new File('CodeNarcReport.txt')
        def reportText = reportFile.text
        reportFile.delete()      // comment out to keep report file around for easy inspection
        assertReportText(reportText, getReportText())
    }

    @Test
    void testWriteReport_WritesToConfiguredReportFile() {
        reportWriter.outputFile = NEW_REPORT_FILE
        reportWriter.writeReport(analysisContext, results)
        def reportFile = new File(NEW_REPORT_FILE)
        def reportText = reportFile.text
        reportFile.delete()
        assertReportText(reportText, getReportText())
    }

    @Test
    void testWriteReport_WritesToStandardOut() {
        reportWriter.writeToStandardOut = true
        def output = captureSystemOut {
            reportWriter.writeReport(analysisContext, results)
        }
        assertReportText(output, getReportText())
    }

    @Test
    void testWriteReport_NullResults() {
        shouldFailWithMessageContaining('results') { reportWriter.writeReport(analysisContext, null) }
    }

    @Test
    void testWriteReport_NullAnalysisContext() {
        shouldFailWithMessageContaining('analysisContext') { reportWriter.writeReport(null, results) }
    }

    @Test
    void testDefaultOutputFile_CodeNarcReport() {
        assert reportWriter.defaultOutputFile == 'CodeNarcReport.txt'
    }

    @Test
    void testMaxPriority_DefaultsTo3() {
        assert reportWriter.maxPriority == 3
    }

    //------------------------------------------------------------------------------------
    // Setup and helper methods
    //------------------------------------------------------------------------------------

    @BeforeEach
    void setUpAbstractTextReportWriterTestCase() {
        reportWriter = createReportWriter()
        reportWriter.getTimestamp = { TIMESTAMP_DATE }

        def srcMainDirResults = new DirectoryResults('src/main')
        def srcMainDaoDirResults = new DirectoryResults('src/main/dao')
        def srcTestDirResults = new DirectoryResults('src/test')
        def srcMainFileResults1 = new FileResults('src/main/MyAction.groovy', [VIOLATION1, VIOLATION3, VIOLATION3, VIOLATION1, VIOLATION2])
        def srcMainFileResults2 = new FileResults('src/main/MyCleanAction.groovy', [])
        def fileResultsMainDao1 = new FileResults('src/main/dao/MyDao.groovy', [VIOLATION3])
        def fileResultsMainDao2 = new FileResults('src/main/dao/MyOtherDao.groovy', [VIOLATION2, VIOLATION1])

        srcMainDirResults.addChild(srcMainFileResults1)
        srcMainDirResults.addChild(srcMainFileResults2)
        srcMainDirResults.addChild(srcMainDaoDirResults)
        srcMainDaoDirResults.addChild(fileResultsMainDao1)
        srcMainDaoDirResults.addChild(fileResultsMainDao2)

        results = new DirectoryResults()
        results.addChild(srcMainDirResults)
        results.addChild(srcTestDirResults)

        analysisContext = new AnalysisContext(sourceDirectories:[SRC_DIR1, SRC_DIR2])
        stringWriter = new StringWriter()
    }

    @SuppressWarnings('JUnitStyleAssertions')
    protected void assertReportText(String actualText, String expectedText) {
        def actualLines = actualText.readLines()
        def expectedLines = expectedText.readLines()
        actualLines.eachWithIndex { line, index ->
            def lineNumber = "$index".padLeft(2)
            println "$lineNumber: $line"
            assertEquals("line=$line", expectedLines[index], line)
        }
        assertEquals(expectedLines.size(), actualLines.size())
    }

    @SuppressWarnings('ConfusingMethodName')
    protected static String version() {
        return VERSION
    }

    protected static String formattedTimestamp() {
        return FORMATTED_TIMESTAMP
    }

}
