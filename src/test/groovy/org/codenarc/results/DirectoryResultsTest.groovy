/*
* Copyright 2008 the original author or authors.
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
package org.codenarc.results

import org.codenarc.rule.StubRule
import org.codenarc.rule.Violation
import org.codenarc.test.AbstractTestCase
import org.junit.jupiter.api.Test

/**
 * Tests for DirectoryResults
 *
 * @author Chris Mair
 */
class DirectoryResultsTest extends AbstractTestCase {

    private static final PATH = '/src/main'
    private static final VIOLATION1 = new Violation(rule:new StubRule(1))
    private static final VIOLATION2 = new Violation(rule:new StubRule(2))
    private static final VIOLATION3 = new Violation(rule:new StubRule(3))
    private static final VIOLATION4 = new Violation(rule:new StubRule(4))
    private static final VIOLATION7 = new Violation(rule:new StubRule(7))

    @Test
    void testNoChildren() {
        def results = new DirectoryResults(PATH)
        assert results.path == PATH
        assert results.children == []
        assert results.violations == []

        assert results.getNumberOfViolationsWithPriority(1) == 0
        assert results.getNumberOfViolationsWithPriority(2) == 0
        assert results.getNumberOfViolationsWithPriority(3) == 0

        assert results.totalNumberOfFiles == 0
        assert results.getNumberOfFilesWithViolations(1) == 0
        assert results.getNumberOfFilesWithViolations(1, false) == 0
        assert !results.isFile()
    }

    @Test
    void testWithOneChild() {
        def results = new DirectoryResults(PATH)
        assert results.path == PATH
        def fileResults = new FileResults('path', [VIOLATION1, VIOLATION3, VIOLATION3, VIOLATION1, VIOLATION2])
        results.addChild(fileResults)
        assert results.children == [fileResults]
        assert results.violations.findAll { v -> v.rule.priority == 1 } == [VIOLATION1, VIOLATION1]
        assert results.violations.findAll { v -> v.rule.priority == 2 } == [VIOLATION2]
        assert results.violations.findAll { v -> v.rule.priority == 3 } == [VIOLATION3, VIOLATION3]

        assert results.getNumberOfViolationsWithPriority(1) == 2
        assert results.getNumberOfViolationsWithPriority(2) == 1
        assert results.getNumberOfViolationsWithPriority(3, false) == 2

        assert results.getNumberOfFilesWithViolations(3) == 1
        assert results.getNumberOfFilesWithViolations(1) == 1
        assert results.getNumberOfFilesWithViolations(1, false) == 1

        assert results.getTotalNumberOfFiles() == 1
        assert results.getTotalNumberOfFiles(false) == 1
    }

    @Test
    void testWithMultipleChildren() {
        def results = new DirectoryResults(PATH)
        assert results.path == PATH
        def fileResults1 = new FileResults('path', [VIOLATION1, VIOLATION3, VIOLATION3, VIOLATION7, VIOLATION1, VIOLATION2])
        results.addChild(fileResults1)
        def subDirResults = new DirectoryResults('subdir')
        def fileResults2 = new FileResults('path', [VIOLATION2, VIOLATION3, VIOLATION4])
        subDirResults.addChild(fileResults2)
        results.addChild(subDirResults)
        assert results.children == [fileResults1, subDirResults]
        assert results.getViolations().sort { v -> v.rule.priority } == [VIOLATION1, VIOLATION1, VIOLATION2, VIOLATION2, VIOLATION3, VIOLATION3, VIOLATION3, VIOLATION4, VIOLATION7]

        assert results.violations.findAll { v -> v.rule.priority == 1 } == [VIOLATION1, VIOLATION1]
        assert results.violations.findAll { v -> v.rule.priority == 2 } == [VIOLATION2, VIOLATION2]
        assert results.violations.findAll { v -> v.rule.priority == 3 } == [VIOLATION3, VIOLATION3, VIOLATION3]
        assert results.violations.findAll { v -> v.rule.priority == 4 } == [VIOLATION4]
        assert results.violations.findAll { v -> v.rule.priority == 7 } == [VIOLATION7]

        assert results.getNumberOfViolationsWithPriority(1) == 2
        assert results.getNumberOfViolationsWithPriority(2) == 2
        assert results.getNumberOfViolationsWithPriority(3) == 3
        assert results.getNumberOfViolationsWithPriority(4) == 1
        assert results.getNumberOfViolationsWithPriority(7) == 1

        assert results.getNumberOfViolationsWithPriority(1, false) == 2
        assert results.getNumberOfViolationsWithPriority(2, false) == 1
        assert results.getNumberOfViolationsWithPriority(3, false) == 2

        assert results.getNumberOfFilesWithViolations(1) == 1
        assert results.getNumberOfFilesWithViolations(2) == 2
        assert results.getNumberOfFilesWithViolations(1, false) == 1
        assert subDirResults.getNumberOfFilesWithViolations(1, false) == 0

        assert results.getTotalNumberOfFiles() == 2
        assert results.getTotalNumberOfFiles(false) == 1
    }

    @Test
    void testRemoveViolation() {
        def results = new DirectoryResults(PATH)
        results.removeViolation(VIOLATION3)
        assert results.getViolations() == []

        def fileResults1 = new FileResults('path', [VIOLATION1, VIOLATION2, VIOLATION3])
        results.addChild(fileResults1)
        assert results.getViolations() == [VIOLATION1, VIOLATION2, VIOLATION3]

        results.removeViolation(VIOLATION3)
        assert results.getViolations() == [VIOLATION1, VIOLATION2]

        def fileResults2 = new FileResults('path2', [VIOLATION4, VIOLATION7])
        results.addChild(fileResults2)
        assert results.getViolations() == [VIOLATION1, VIOLATION2, VIOLATION4, VIOLATION7]
        results.removeViolation(VIOLATION4)
        assert results.getViolations() == [VIOLATION1, VIOLATION2, VIOLATION7]
    }

    @Test
    void testFindResultsForPath() {
        def results = new DirectoryResults(PATH)
        def fileResults1 = new FileResults('file1', [])
        def subDirResults = new DirectoryResults('subdir')
        def fileResults2 = new FileResults('file2', [])
        subDirResults.addChild(fileResults2)
        results.addChild(fileResults1)
        results.addChild(subDirResults)

        assert results.findResultsForPath(null) == null
        assert results.findResultsForPath('xx/yy') == null
        assert results.findResultsForPath(PATH) == results
        assert results.findResultsForPath('file1') == fileResults1
        assert results.findResultsForPath('subdir') == subDirResults
        assert results.findResultsForPath('file2') == fileResults2
    }

    @Test
    void testGetViolations_ReturnsDefensiveCopy() {
        def results = new DirectoryResults(PATH)
        def fileResults = new FileResults('path', [VIOLATION1, VIOLATION3])
        results.addChild(fileResults)
        results.getViolations() << VIOLATION7
        assert results.getViolations() == [VIOLATION1, VIOLATION3]
    }

    @Test
    void testAddFileResultRecursive() {
        // Add various results
        def results = new DirectoryResults('root')
        def fileResults1 = new FileResults('root/subdir1/file1.groovy', [VIOLATION1, VIOLATION2])
        results.addFileResultRecursive(fileResults1)
        def fileResults2 = new FileResults('root/subdir1/file2.groovy', [VIOLATION1, VIOLATION2])
        results.addFileResultRecursive(fileResults2)
        def fileResults3 = new FileResults('root/file.groovy', [VIOLATION1, VIOLATION2])
        results.addFileResultRecursive(fileResults3)
        def fileResults5 = new FileResults('root/subdir2/subdir3/subdir4/file5.groovy', [VIOLATION1, VIOLATION2])
        results.addFileResultRecursive(fileResults5)

        // Check results are correctly built
        DirectoryResults rootRes = results.findResultsForPath('root')
        assert rootRes != null
        FileResults fileRes = rootRes.findResultsForPath('root/file.groovy')
        assert fileRes == fileResults3
        DirectoryResults subdir1Results = rootRes.findResultsForPath('root/subdir1')
        assert subdir1Results != null
        assert subdir1Results.getChildren() == [fileResults1, fileResults2]
        DirectoryResults subdir2Results = rootRes.findResultsForPath('root/subdir2')
        assert subdir2Results != null
        DirectoryResults subdir3Results = subdir2Results.findResultsForPath('root/subdir2/subdir3')
        assert subdir3Results != null
        DirectoryResults subdir4Results = subdir3Results.findResultsForPath('root/subdir2/subdir3/subdir4')
        assert subdir4Results != null
        FileResults file5Res = subdir4Results.findResultsForPath('root/subdir2/subdir3/subdir4/file5.groovy')
        assert file5Res == fileResults5
    }

}
