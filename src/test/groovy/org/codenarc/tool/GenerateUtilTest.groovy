/*
 * Copyright 2026 the original author or authors.
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
package org.codenarc.tool

import org.codenarc.rule.Rule
import org.codenarc.test.AbstractTestCase
import org.junit.jupiter.api.Test

/**
 * Tests for GenerateUtil
 *
 * @author Chris Mair
  */
class GenerateUtilTest extends AbstractTestCase {

    @Test
    void testGetRuleExtraInformation_ReturnsExpectedValuesFromPropertiesFile() {
        def ruleExtraInformation = GenerateUtil.getRuleExtraInformation()
        assert ruleExtraInformation['AbcMetric'] == 'Requires the GMetrics jar'
        assert ruleExtraInformation['CrapMetric'] == 'Requires the GMetrics jar and a Cobertura coverage file'
        assert ruleExtraInformation['CyclomaticComplexity'] == 'Requires the GMetrics jar'
    }

    @Test
    void testGetRuleExtraInformation_ReturnsNullForRuleNotInPropertiesFile() {
        def ruleExtraInformation = GenerateUtil.getRuleExtraInformation()
        assert ruleExtraInformation['UnknownRule'] == null
    }

    @Test
    void testGetRuleExtraInformation_CachesAndReturnsSameInstanceAcrossCalls() {
        def ruleExtraInformation1 = GenerateUtil.getRuleExtraInformation()
        def ruleExtraInformation2 = GenerateUtil.getRuleExtraInformation()
        assert ruleExtraInformation1.is(ruleExtraInformation2)
    }

    @Test
    void testCreateSortedListOfAllRules_ReturnsAllRulesFromAllPredefinedRuleSetsSortedByName() {
        def rules = GenerateUtil.createSortedListOfAllRules()

        def ruleNames = rules*.name
        assert ruleNames.contains('EmptyCatchBlock')
        assert ruleNames.contains('UnnecessaryBigDecimalInstantiation')
        assert ruleNames == ruleNames.sort(false)
    }

    @Test
    void testSortRules_ReturnsRulesSortedByName() {
        def ruleB = [getName: { 'RuleB' }] as Rule
        def ruleC = [getName: { 'RuleC' }] as Rule
        def ruleA = [getName: { 'RuleA' }] as Rule

        def sorted = GenerateUtil.sortRules([ruleB, ruleC, ruleA])
        assert sorted == [ruleA, ruleB, ruleC]
    }

    @Test
    void testSortRules_EmptyList_ReturnsEmptyList() {
        assert GenerateUtil.sortRules([]) == []
    }

    @Test
    void testSortRules_DoesNotModifyInputList() {
        def ruleB = [getName: { 'RuleB' }] as Rule
        def ruleA = [getName: { 'RuleA' }] as Rule
        def input = [ruleB, ruleA]

        GenerateUtil.sortRules(input)

        assert input == [ruleB, ruleA]
    }

}
