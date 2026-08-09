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

import org.codehaus.groovy.ast.ClassNode
import org.codenarc.rule.AbstractRule
import org.codenarc.rule.Violation
import org.codenarc.source.SourceCode

/**
 * Checks that each source file contains only one top-level class / enum / interface.
 *
 * @author Robert Insley
 */
class OneTopLevelClassRule extends AbstractRule {

    String name = 'OneTopLevelClass'
    int priority = 3

    @Override
    void applyTo(SourceCode sourceCode, List<Violation> violations) {
        if (!sourceCode.name) {
            return
        }

        List<ClassNode> classes = sourceCode?.ast?.classes
        if (classes.any { it.isScript() }) {
            return
        }

        List<ClassNode> topLevelClasses = classes?.findAll { !it.outerClass }
        if (topLevelClasses?.size() > 1) {
            topLevelClasses.eachWithIndex { ClassNode topLevelClass, Integer index ->
                if (index > 0) {
                    violations << createViolation(sourceCode, topLevelClass,
                        "`${sourceCode.name}` contains more than one top-level class / enum / interface. " +
                        "Move `${topLevelClass.nameWithoutPackage}` to its own file.")
                }
            }
        }
    }

    String classNodeType(ClassNode classNode) {
        return classNode.isInterface() ? 'Interface' :
               classNode.isEnum() ? 'Enum' :
               'Class'
    }
}
