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
import java.util.List;
import java.util.Map;

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
    enum Variable {

        /** */
        Init, Negative, Positive, Match_Expected, Miss_Expected
    }

    /** */
    enum Trans {

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
        final List<List<String>> variables = new ArrayList<List<String>>();

        variables.add(new ArrayList<String>(Arrays
            .asList(new String[] { Variable.Init.name() })));

        variables.add(new ArrayList<String>(Arrays.asList(new String[] {
                Variable.Negative.name(),
                Variable.Positive.name() })));

        variables.add(new ArrayList<String>(Arrays.asList(new String[] {
                Variable.Match_Expected.name(),
                Variable.Miss_Expected.name() })));

        final StringBuilder ruleBuilder = new StringBuilder()
            .append(TEST_NEG)
            .append(':')
            .append(Variable.Negative.name())
            .append('&')
            .append(Variable.Match_Expected.name())
            .append('|')
            .append(Variable.Positive.name())
            .append('&')
            .append(Variable.Miss_Expected.name());

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
        final ScenarioSource<Object> source = new ScenarioSource<Object>(this);

        source.addObserver(Variable.Init, new CaseObserver<Object>() {

            @Override
            public void prepareCase(final Object caseRaw)
            {
                @SuppressWarnings("unchecked")
                final CaseFixture<String> mockFixture = Mockito
                    .mock(CaseFixture.class);

                final String[] pairArr = TEST_PAIR.split(":");
                final Map<String, String> pairMap = new HashMap<String, String>();
                pairMap.put(pairArr[0], pairArr[1]);
                final Map<String, String> reversePairMap = new HashMap<String, String>();
                reversePairMap.put(pairArr[1], pairArr[0]);

                Mockito.doReturn(pairMap).when(mockFixture).getPairMap();
                Mockito
                    .doReturn(reversePairMap)
                    .when(mockFixture)
                    .getReversePairMap();
                setTransientValue(Trans.Fixture, mockFixture);
            }
        });


        //@formatter:off
        source.addTransientCase(Trans.Output, TEST_NEG, Variable.Negative);
        source.addTransientCase(Trans.Output, TEST_POS, Variable.Positive);
        source.addTransientCase(Trans.Expected, true, Variable.Match_Expected);
        source.addTransientCase(Trans.Expected, false, Variable.Miss_Expected);
        //@formatter:on

        source.notifyObservers();
    }

    /** {@inheritDoc} */
    @Override
    protected void execute()
    {
        final String ruleOutput = (String) getTransientValue(Trans.Output);
        @SuppressWarnings("unchecked")
        final CaseFixture<String> fixture = (CaseFixture<String>) getTransientValue(Trans.Fixture);
        final boolean expected = (Boolean) getTransientValue(Trans.Expected);

        setResult(getMockSubject().getBinaryOutput(
            ruleOutput,
            fixture,
            expected));
    }

}