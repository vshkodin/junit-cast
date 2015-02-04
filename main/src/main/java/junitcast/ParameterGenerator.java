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
package junitcast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junitcast.rule.Rule;
import junitcast.rule.RuleEvaluator;
import junitcast.rule.RuleProcessor;
import junitcast.util.Constant;

import org.junit.Assert;

/**
 * Implementation for junit4 parameterized test generateData static method.
 *
 * @param <T> scenario element type.
 * @author Royce Remulla
 */
public class ParameterGenerator<T> {


    /**
     * Generate Parameterized Collection based on a given resource bundle uri.
     *
     * @param resourceUri resource bundle uri.
     * @deprecated Use {@link #genVarData(String)}.
     */
    @Deprecated
    public Collection<Object[]> generateData(final String resourceUri)
    {
        return genVarData(resourceUri);
    }


    /**
     * Generate parameters from set of variables. This will calculate every
     * possible combination minus any defined exemption.
     *
     * @param resourceUri resource bundle uri.
     */
    @SuppressWarnings(Constant.Warning.UNCHECKED)
    public Collection<Object[]> genVarData(final String resourceUri)
    {
        final ResourceFixture resFixFactory = new ResourceFixture(resourceUri);
        return generateData((List<CaseFixture<T>>) resFixFactory.getFixtures());
    }

    /**
     * Generate parameters from a fixed set of test data.
     *
     * @param resourceUri resource bundle uri.
     */
    @SuppressWarnings(Constant.Warning.UNCHECKED)
    public Collection<Object[]> genFixedData(final String resourceUri)
    {
        final ResourceFixture resFixFactory = new ResourceFixture(resourceUri);
        return generateData(
            (List<CaseFixture<T>>) resFixFactory.getFixtures(),
            false);
    }


    /**
     * @param fixTureList list of test cases.
     */
    public Collection<Object[]> generateData(
            final List<CaseFixture<T>> fixTureList)
    {
        return generateData(fixTureList, true);
    }


    /**
     * @param fixTureList list of test cases.
     * @param isComputed false when data is fixed list other wise it is the
     *            combination of all variables.
     */
    @SuppressWarnings(Constant.Warning.UNCHECKED)
    public Collection<Object[]> generateData(
            final List<CaseFixture<T>> fixTureList, final boolean isComputed)
    {
        final List<Object[]> retval = new ArrayList<Object[]>();
        for (final CaseFixture<T> caseFixture : fixTureList) {
            if (isComputed) {
                addCase(retval, caseFixture);
            } else {
                addFixedCase(retval, caseFixture);
            }
        }

        if (isComputed) {
            Collections.sort(retval, new Comparator<Object[]>() {

                @Override
                @SuppressWarnings("PMD.UseVarargs")
                public int compare(final Object[] paramArr1,
                        final Object[] paramArr2)
                {
                    final Parameter<T> param1 = (Parameter<T>) paramArr1[0];
                    final Parameter<T> param2 = (Parameter<T>) paramArr2[0];
                    return param1.toString().compareTo(param2.toString());
                }
            });
        }
        return retval;
    }

    /**
     * Add a case in a parameterized test case.
     *
     * @param paramCollection the List of Object array in parameterized test.
     * @param caseFixture the case fixture.
     */
    private void addCase(final List<Object[]> paramCollection,
            final CaseFixture<T> caseFixture)
    {
        final ListMerger<T> listCombinator = new ListMerger<T>();
        final List<List<T>> combinations = listCombinator.merge(caseFixture
            .getVariables());

        for (final List<T> scenario : combinations) {
            if (isValidCase(scenario, caseFixture)) {
                final String result = validateRule(scenario, caseFixture);
                paramCollection
                    .add(new Object[] { new Parameter<T>(caseFixture //NOPMD: False positive.
                        .getCaseDesc(), scenario, result, caseFixture
                        .getCaseId()) });
            }

        }
    }

    /**
     * Add a case in a parameterized test case.
     *
     * @param paramCollection the List of Object array in parameterized test.
     * @param caseFixture the case fixture.
     */
    @SuppressWarnings(Constant.Warning.UNCHECKED)
    private void addFixedCase(final List<Object[]> paramCollection,
            final CaseFixture<T> caseFixture)
    {
        for (final List<T> scenario : caseFixture.getVariables()) {
            @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
            final List<?> scenList = new ArrayList<Object>(scenario);

            final String result = validateRule((List<T>) scenList, caseFixture);
            paramCollection.add(new Object[] { new Parameter<T>(caseFixture //NOPMD: False positive.
                .getCaseDesc(), (List<T>) scenList, result, caseFixture
                .getCaseId()) });
        }
    }


    /**
     * Assertion exception when rule failed. Otherwise the single rule that
     * succeeded is returned.
     *
     * @param scenario current Test scenario.
     * @param fixture test fixture.
     */
    public String validateRule(final List<T> scenario,
            final CaseFixture<T> fixture)
    {
        final Rule rule = fixture.getRule();

        String retval = null; //NOPMD: null default, conditionally redefine.
        final Boolean[] ruleResult = new RuleProcessor<T>().evaluate(
            scenario,
            fixture);

        final boolean singleResult = rule.getActionList().size() == 1;
        if (singleResult) {
            final boolean nextResult = ruleResult[0];
            final String action = rule.getActionList().get(0);
            retval = getBinaryOutput(action, fixture, nextResult);
        } else {
            final Set<String> matchedOutputs = new HashSet<String>();
            int matchCount = 0; //NOPMD: counter, redefined inside loop.
            for (int i = 0; i < ruleResult.length; i++) {
                if (ruleResult[i]) {
                    matchCount++; //NOPMD: counter, redefined inside loop.
                    retval = rule.getActionList().get(i);
                    matchedOutputs.add(retval);
                }
            }

            Assert.assertEquals(
                "Scenario must fall into a unique rule output/clause: "
                        + scenario + ", matched: " + matchedOutputs,
                1,
                matchCount);
        }
        return retval;
    }

    /**
     *
     * @param ruleOutput output default output specified by the binary rule.
     * @param fixture test case fixture.
     * @param expected expected output.
     */
    String getBinaryOutput(final String ruleOutput,
            final CaseFixture<T> fixture, final boolean expected)
    {
        final boolean isNegative = fixture.getPairMap().containsKey(ruleOutput);
        String actualOutput;
        if (isNegative) {
            if (expected) {
                actualOutput = ruleOutput;
            } else {
                actualOutput = getOpposite(ruleOutput, fixture);
            }
        } else {
            if (expected) {
                actualOutput = ruleOutput;
            } else {
                actualOutput = getOpposite(ruleOutput, fixture);
            }
        }
        return actualOutput;
    }

    /**
     * @param ruleOutput output default output specified by the binary rule.
     * @param fixture test case fixture.
     */
    String getOpposite(final String ruleOutput, final CaseFixture<T> fixture)
    {
        String actionOpposite;
        if (fixture.getPairMap().get(ruleOutput) == null) {
            actionOpposite = fixture.getReversePairMap().get(ruleOutput);
        } else {
            actionOpposite = fixture.getPairMap().get(ruleOutput);
        }
        return actionOpposite;
    }

    /**
     * Custom invalid, and for UI null facility status is unexpected.
     *
     * @param scenario current Test scenario.
     * @param fixture test case fixture.
     */
    @SuppressWarnings("PMD.BooleanInversion")
    private boolean isValidCase(final List<T> scenario,
            final CaseFixture<T> fixture)
    {
        boolean valid;
        if (fixture.getExemptRule() == null) {
            valid = true;
        } else {
            final String exemptRule = fixture.getExemptRule();
            final RuleEvaluator<T> ruleEvaluator = new RuleEvaluator<T>(
                fixture.getConverters());
            ruleEvaluator.parse(exemptRule);
            valid = !ruleEvaluator.evaluate(
                scenario,
                fixture.getRuleConverter()); //NOPMD: False positive.
        }
        return valid;
    }
}
