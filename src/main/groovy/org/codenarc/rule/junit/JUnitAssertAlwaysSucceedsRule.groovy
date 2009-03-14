/*
 * Copyright 2009 the original author or authors.
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
package org.codenarc.rule.junit

import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codehaus.groovy.ast.expr.ConstantExpression

/**
 * Rule that checks for JUnit <code>assert()</code> method calls with constant arguments
 * such that the assertion always succeeds. This includes:
 * <ul>
 *   <li><code>assertTrue(true)</code>.</li>
 *   <li><code>assertFalse(false)</code>.</li>
 *   <li><code>assertNull(null)</code>.</li>
 * </ul>
 * This rule sets the default value of <code>applyToFilesMatching</code> to only match source code file
 * paths ending in 'Test.groovy' or 'Tests.groovy'.
 *
 * @author Chris Mair
 * @version $Revision$ - $Date$
 */
class JUnitAssertAlwaysSucceedsRule extends AbstractAstVisitorRule {
    String name = 'JUnitAssertAlwaysSucceeds'
    int priority = 2
    Class astVisitorClass = JUnitAssertAlwaysSucceedsAstVisitor
    String applyToFilesMatching = DEFAULT_TEST_FILES
}

class JUnitAssertAlwaysSucceedsAstVisitor extends AbstractAstVisitor  {

    void visitMethodCallExpression(MethodCallExpression methodCall) {
        def isMatch =
            JUnitUtil.isAssertConstantValueCall(methodCall, 'assertTrue', ConstantExpression.TRUE) ||
            JUnitUtil.isAssertConstantValueCall(methodCall, 'assertFalse', ConstantExpression.FALSE) ||
            JUnitUtil.isAssertConstantValueCall(methodCall, 'assertNull', ConstantExpression.NULL)
        if (isMatch) {
            addViolation(methodCall)
        }
        super.visitMethodCallExpression(methodCall)
    }

}