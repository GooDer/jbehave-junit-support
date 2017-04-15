/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jbehave.support.runner

import org.jbehave.support.runner.story.PendingStepStories
import org.junit.runner.Description
import org.junit.runner.notification.RunNotifier
import spock.lang.Shared
import spock.lang.Specification

/**
 * @author Michal Bocek
 * @since 06/10/16
 */
class PendingStepStoriesTest extends Specification {

    @Shared
    runner = new JUnitRunner(PendingStepStories)
    def notifier = Mock(RunNotifier)

    def "Test correct notifications"() {
        when:
        runner.run(notifier)

        then:
        1 * notifier.fireTestStarted({it.displayName.startsWith("BeforeStories")} as Description)
        then:
        1 * notifier.fireTestFinished({it.displayName.startsWith("BeforeStories")} as Description)
        then:
        1 * notifier.fireTestStarted({it.displayName.equals("Story: PendingStep")} as Description)
        then:
        1 * notifier.fireTestStarted({it.displayName.equals("Scenario: Pending step")} as Description)
        then:
        1 * notifier.fireTestStarted({it.displayName.contains("When Auditing user")} as Description)
        then:
        1 * notifier.fireTestFinished({it.displayName.contains("When Auditing user")} as Description)
        then:
        1 * notifier.fireTestIgnored({it.displayName.contains("When User signing in")} as Description)
        then:
        1 * notifier.fireTestIgnored({it.displayName.contains("Then User with name Tester is properly signed in")} as Description)
        then:
        1 * notifier.fireTestFinished({it.displayName.equals("Scenario: Pending step")} as Description)
        then:
        1 * notifier.fireTestFinished({it.displayName.equals("Story: PendingStep")} as Description)
        then:
        1 * notifier.fireTestStarted({it.displayName.startsWith("AfterStories")} as Description)
        then:
        1 * notifier.fireTestFinished({it.displayName.startsWith("AfterStories")} as Description)
    }

    def "Test descriptions"() {
        when:
        def desc = runner.description
        def children = desc.children

        then:
        desc.testClass == PendingStepStories
        children.size() == 3
        children[0].displayName =~ /BeforeStories.*/
        children[1].displayName == "Story: PendingStep"
        children[1].children[0].displayName == "Scenario: Pending step"
        children[1].children[0].children.size() == 3
        children[1].children[0].children[0].displayName =~ /When Auditing user(.*)/
        children[1].children[0].children[1].displayName =~ /When User signing in/
        children[1].children[0].children[2].displayName =~ /Then User with name Tester is properly signed in/
        children[2].displayName =~ /AfterStories.*/
    }
}