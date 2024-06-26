---
layout: default
title: CodeNarc - Developer Guide
---  

# CodeNarc - Developer Guide

**Contents**
  * [The codenarc Command-line Script](#the-codenarc-command-line-script)
  * [New Rule Checklist](#new-rule-checklist)
  * [Before You Submit a Pull Request or Patch](#before-you-submit-a-pull-request-or-patch)
  * [Intellij IDEA Support](#intellij-idea-support)


## The codenarc Command-line Script

There is a `codenarc.groovy` command-line script in the root directory of the project.
It is intended to streamline common development tasks. Currently, it supports a
`create-rule` task for creating new **CodeNarc** rules.

### codenarc create-rule

The `create-rule` task performs the following steps:
  * Prompt for the rule name
  * Prompt for the existing ruleset (category) name to which the rule is added
  * Create a new rule class file in the proper package (under src/main/groovy)
  * Create a new rule test class file (under src/test/groovy)
  * Add placeholder description messages for the new rule to "codenarc-base-messages.properties"
  * Add the new rule to the chosen ruleset XML file

**RUNNING**

On **Unix/Mac**, you can run the following from the project root directory:

      ./codenarc create-rule

On **Windows**, you can run:

      codenarc create-rule

(If **Windows** is not configured to automatically run *.groovy files,
just can run `groovy codenarc create-rule`)

**AFTER YOU RUN** `codenarc create-rule`

After you run `codenarc create-rule`, finish up the rule implementation, including the following:
  1. Edit the generated rule class and associated test class to add the proper implementation.
  2. Modify the description messages for the new rule in "codenarc-base-messages.properties". Move the
     message entries under the proper ruleset/category section within the file.
  4. Add description to "codenarc-rules-[category].md" document in the "docs" folder.


## New Rule Checklist

Perform the following steps when creating a new rule. See [The codenarc Command-line Script](#the-codenarc-command-line-script).
for information on the command-line script that automates a good bit of the boilerplate, as indicated below.
  1. Implement the new *Rule* class. This is typically a subclass of `AbstractAstVisitorRule`. [1]
  2. Implement the associated *Rule* test class. This is typically a subclass of `AbstractRuleTestCase`. [1]
  3. Add the new rule class name to the appropriate *RuleSet* file under "src/main/resources/rulesets". [1]
  4. Add the new rule description entries to "src/main/resources/codenarc-base-messages.properties".
     This includes both "*RuleName*.description" and "*RuleName*.description.html" property entries. [1]
  5. Run `LoadAllPredefinedRuleSetsTest`.
  6. Add a description of the new rule to "codenarc-rules-[category].md" document in the "docs" folder.

###  NOTES

  * [1] These files are created (skeletons) or updated automatically if you run the `codenarc create-rule`
        script to create the rule. See [The codenarc Command-line Script](#the-codenarc-command-line-script).


## Before You Submit a Pull Request or Patch

Please do the following before submitting a pull request or patch:

  * Run the full **CodeNarc** test suite. This includes a test called `RunCodeNarcAgainstProjectSourceCodeTest`
    that runs **CodeNarc** against its own source code (including any code that you have added or changed).

  * Submit small PRs (typically less than 300 lines). If you need to make more extensive/larger changes, please create an issue describing the overall changes and then create multiple smaller PRs that link to that issue. Respect your reviewers. Why small PRs:
    - [Anatomy of a perfect pull request](https://opensource.com/article/18/6/anatomy-perfect-pull-request#:~:text=Recap-,Pull%20request%20size,of%20250%20lines%20of%20change.)
    - [Best Practices for Code Review](https://smartbear.com/learn/code-review/best-practices-for-peer-code-review/)
    - [The Size of the Pull/Merge Request is More Important Than You Think](https://www.linkedin.com/pulse/size-pullmerge-request-more-important-than-you-think-rodrigo-miguel/)

## Intellij IDEA Support

To generate Intellij IDEA files, execute `gradle idea`
