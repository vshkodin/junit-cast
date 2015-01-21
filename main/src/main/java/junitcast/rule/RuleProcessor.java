/**
 *   Copyright 2013 Royce Remulla
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License. 
 */
package junitcast.rule;

import java.util.List;

import junitcast.CaseFixture;
import junitcast.converter.ElementConverter;

/**
 * @param <T> scenario element type.
 * @author Royce Remulla
 */
public class RuleProcessor<T> {

    /**
     * @param scenario current scenario.
     * @param caseFixture current test case fixture.
     */
    public boolean[] evaluate(final List<T> scenario, final CaseFixture<T> caseFixture)
    {
        if (scenario == null || caseFixture == null) {
            throw new IllegalArgumentException("Scenario or fixture cannot be null.");
        }
        final Rule rule = caseFixture.getRule();

        final int size = rule.getActionList().size();
        final boolean[] retval = new boolean[size];

        int counter = 0;

        final RuleEvaluator<T> ruleEvaluator = getRuleEvaluator(caseFixture.getConverters());
        for (final String action : rule.getActionList()) {
            final String ruleClause = rule.getRuleClause(action);

            ruleEvaluator.parse(ruleClause);
            retval[counter] = ruleEvaluator.evaluate(scenario, caseFixture.getRuleConverter());
            counter++;
        }

        return retval;
    }

    /**
     * @param elementConverter List of element converters for this case.
     */
    RuleEvaluator<T> getRuleEvaluator(final List<ElementConverter> elementConverter)
    {
        return new RuleEvaluator<T>(elementConverter);
    }

}