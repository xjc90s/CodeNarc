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
package org.codenarc.rule.convention

import org.codenarc.rule.AbstractRuleTestCase
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Tests for OneTopLevelClassRule
 *
 * @author Robert Insley
 */
class OneTopLevelClassRuleTest extends AbstractRuleTestCase<OneTopLevelClassRule> {

    @BeforeEach
    void setup() {
        sourceCodeName = 'MyClass.groovy'
    }

    @Test
    void testRuleProperties() {
        assert rule.priority == 3
        assert rule.name == 'OneTopLevelClass'
    }

    @Test
    void testNoViolationsOnClass() {
        final SOURCE = '''
            class MyClass {
                int myInt
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testNoViolationsOnEnum() {
        final SOURCE = '''
            enum MyEnum {
                OPTION_ONE, OPTION_TWO
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testNoViolationsOnInterface() {
        final SOURCE = '''
            interface MyInterface {}
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testNoViolationsOnInnerTypes() {
        final SOURCE = '''
            class MyClass {
                class InnerClass {}
                enum InnerEnum {
                    OPTION_ONE, OPTION_TWO
                }
                interface InnerInterface {}
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testTwoClasses_Violation() {
        final SOURCE = '''
            class FirstClass {}
            class SecondClass {}
        '''
        assertSingleViolation(SOURCE, 3, 'class SecondClass {}',
            '`MyClass.groovy` contains more than one top-level class / enum / interface. Move `SecondClass` to its own file.')
    }

    @Test
    void testThreeClasses_MultipleViolations() {
        final SOURCE = '''
            class FirstClass {}
            class SecondClass {}
            class ThirdClass {}
        '''
        assertViolations(SOURCE,
            [line: 3, source: 'class SecondClass {}',
             message: '`MyClass.groovy` contains more than one top-level class / enum / interface. Move `SecondClass` to its own file.'],
            [line: 4, source: 'class ThirdClass {}',
             message: '`MyClass.groovy` contains more than one top-level class / enum / interface. Move `ThirdClass` to its own file.'])
    }

    @Test
    void testTwoEnums_Violation() {
        final SOURCE = '''
            enum EnumOne {
                OPTION_ONE, OPTION_TWO
            }
            enum EnumTwo {
                OPTION_A, OPTION_B
            }
        '''
        assertSingleViolation(SOURCE, 5, 'enum EnumTwo {',
            '`MyClass.groovy` contains more than one top-level class / enum / interface. Move `EnumTwo` to its own file.')
    }

    @Test
    void testTwoInterfaces_Violation() {
        final SOURCE = '''
            interface InterfaceOne {}
            interface InterfaceTwo {}
        '''
        assertSingleViolation(SOURCE, 3, 'interface InterfaceTwo {}',
            '`MyClass.groovy` contains more than one top-level class / enum / interface. Move `InterfaceTwo` to its own file.')
    }

    @Test
    void testThreeTypes_MultipleViolations() {
        final SOURCE = '''
            class FirstClass {}
            enum MyEnum {
                OPTION_ONE, OPTION_TWO
            }
            interface MyInterface {}
        '''
        assertViolations(SOURCE,
            [line: 3, source: 'enum MyEnum {',
             message: '`MyClass.groovy` contains more than one top-level class / enum / interface. Move `MyEnum` to its own file.'],
            [line: 6, source: 'interface MyInterface {}',
             message: '`MyClass.groovy` contains more than one top-level class / enum / interface. Move `MyInterface` to its own file.'])
    }

    @Test
    void testNoViolationsOnScript() {
        final SOURCE = '''
            println 'Hello'
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testNoViolationsOnScriptWithClass() {
        final SOURCE = '''
            println 'Hello'
            class MyClass {}
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testNoViolationsOnScriptWithMultipleClasses() {
        final SOURCE = '''
            println 'Hello'
            class FirstClass {}
            class SecondClass {}
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testNoViolationsOnScriptWithMultipleTypes() {
        final SOURCE = '''
            println 'Hello'
            enum MyEnum {
                OPTION_ONE, OPTION_TWO
            }
            interface MyInterface {}
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testNoViolationsOnEmptySource() {
        final SOURCE = '''
        '''
        assertNoViolations(SOURCE)
    }

    @Override
    protected OneTopLevelClassRule createRule() {
        new OneTopLevelClassRule()
    }
}
