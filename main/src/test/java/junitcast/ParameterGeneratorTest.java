/**
 *  Copyright 2013 Royce Remulla
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
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junitcast.rule.Rule;

import org.junit.runners.Parameterized.Parameters;
import org.mockito.Mockito;

/**
 * Test class for {@link ParameterGenerator}. Using java fixture instead of
 * property file. Using property file is cleaner by far.
 *
 * @author Royce Remulla
 */
public class ParameterGeneratorTest
extends
AbstractTransientValueTestCase<ParameterGenerator<String>, Object, Object> {


    /** Test artifact. */
    private static final String TEST_NEG = "NEGATIVE";

    /** Test artifact. */
    private static final String TEST_POS = "POSITIVE";

    /** Test artifact. */
    private static final String TEST_PAIR = TEST_NEG + ":" + TEST_POS;


    /** @param pParameter Data Transfer Object Parameter in Parameterized test. */
    public ParameterGeneratorTest(final Parameter<Object> pParameter) {
        super(pParameter);
    }


    /** {@inheritDoc} */
    @Override
    protected void setupTargetObject(final List<Object> constructorParams)
    {
        new MockitoHelper().setupTargetObject(this, constructorParams);
    }

    /** */
    private enum Variable {

        /** */
        Init, Negative, Positive, Matched_Expected, Missed_Expected
    }

    /** */
    private enum Trans {

        /** */
        Output, Fixture, Expected, Pair
    }

    /**
     * <pre>
     * Test data generator.
     * This method is called by the JUnit parameterized test runner and
     * returns a Collection of Arrays.  For each Array in the Collection,
     * each array element corresponds to a parameter in the constructor.
     * </pre>
     */
    @Parameters(name = "{0}")
    public static Collection<Object[]> generateData()
    {
        final List<CaseFixture<String>> fixTureList = new ArrayList<CaseFixture<String>>();
        final List<Set<String>> variables = new ArrayList<Set<String>>();
        variables.add(new LinkedHashSet<String>(Arrays
                .asList(new String[] { Variable.Init.name() })));
        variables.add(new LinkedHashSet<String>(Arrays.asList(new String[] {
                Variable.Negative.name(),
                Variable.Positive.name() })));
        variables.add(new LinkedHashSet<String>(Arrays.asList(new String[] {
                Variable.Matched_Expected.name(),
                Variable.Missed_Expected.name() })));

        final StringBuilder ruleBuilder = new StringBuilder()
        .append(TEST_NEG)
        .append(':')
        .append(
            Variable.Negative.name() + '&'
            + Variable.Matched_Expected.name())
            .append('|');
        ruleBuilder.append(Variable.Positive.name() + '&'
            + Variable.Missed_Expected.name());
        fixTureList.add(new CaseFixture<String>(
                "getBinaryAction",
                variables,
                new Rule(ruleBuilder.toString()),
                TEST_PAIR));

        return new ParameterGenerator<String>().generateData(fixTureList);
    }

    /** {@inheritDoc} */
    @Override
    protected void prepare()
    {

        for (final Object scenarioToken : getParameter().getScenario()) {
            final Variable variable = Variable.valueOf(scenarioToken
                .toString()
                .replaceAll(" ", "_"));
            switch (variable) {
                case Init:
                    @SuppressWarnings("unchecked")
                    final CaseFixture<String> mockFixture = Mockito
                    .mock(CaseFixture.class);

                    //blech!
                    final String[] pairArr = TEST_PAIR.split(":");
                    final Map<String, String> pairMap = new HashMap<String, String>();
                    pairMap.put(pairArr[0], pairArr[1]);
                    final Map<String, String> reverse = new HashMap<String, String>();
                    reverse.put(pairArr[1], pairArr[0]);
                    Mockito.doReturn(pairMap).when(mockFixture).getPairMap();
                    Mockito
                    .doReturn(reverse)
                    .when(mockFixture)
                    .getReversePairMap();
                    setTransientValue(Trans.Fixture.ordinal(), mockFixture);
                    break;

                case Negative:
                    setTransientValue(Trans.Output.ordinal(), TEST_NEG);
                    break;
                case Positive:
                    setTransientValue(Trans.Output.ordinal(), TEST_POS);
                    break;
                case Matched_Expected:
                    setTransientValue(Trans.Expected.ordinal(), true);
                    break;
                case Missed_Expected:
                    setTransientValue(Trans.Expected.ordinal(), false);
                    break;
                default:

                    break;
            }

        }

    }

    /** {@inheritDoc} */
    @Override
    protected Object execute()
    {
        final String ruleOutput = (String) getTransientValue(Trans.Output
            .ordinal());
        @SuppressWarnings("unchecked")
        final CaseFixture<String> fixture = (CaseFixture<String>) getTransientValue(Trans.Fixture
            .ordinal());
        final boolean expected = (Boolean) getTransientValue(Trans.Expected
            .ordinal());

        return getMockSubject().getBinaryOutput(ruleOutput, fixture, expected);
    }

}