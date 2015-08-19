/**
 *   Copyright 2014 Royce Remulla
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

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import junitcast.AbstractTransientValueTestCase;
import junitcast.CaseFixture;
import junitcast.CaseObserver;
import junitcast.MockitoHelper;
import junitcast.Parameter;
import junitcast.ParameterGenerator;
import junitcast.ScenarioSource;
import junitcast.converter.ElementConverter;

import org.junit.runners.Parameterized.Parameters;
import org.mockito.Matchers;
import org.mockito.Mockito;

/**
 * Test class for RuleProcessor.
 *
 * @author Royce Remulla
 */
public class RuleProcessorTest extends
        AbstractTransientValueTestCase<RuleProcessor<String>, String, Object> {

    /**
     * @param pParameter Data Transfer Object Parameter in Parameterized test.
     */
    public RuleProcessorTest(final Parameter<String> pParameter) {
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
        null_scenario, valid_scenario, null_rule, valid_rule, parse_exception,
        /** */
        parse_ok, evaluate_exception, evaluate_ok
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
        return new ParameterGenerator<String>()
            .genVarData("junitcast.rule.RuleProcessorTest");
    }


    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    @Override
    protected void prepare()
    {
        final RuleEvaluator<String> mockRuleEval =
                Mockito.mock(RuleEvaluator.class);
        Mockito
            .doReturn(mockRuleEval)
            .when(getMockSubject())
            .getRuleEvaluator((List<ElementConverter>) Matchers.any());


        final ScenarioSource<String> scenSrc = new ScenarioSource<String>(this);

        scenSrc.addObserver(
            Variable.valid_scenario,
            new CaseObserver<String>() {

                @Override
                public void prepareCase(final int index, final String caseRaw)
                {
                    setTransientValue(
                        Trans.Scenario,
                        Arrays.asList(new String[] { "test scenario" }));
                }
            });

        scenSrc.addObserver(Variable.valid_rule, new CaseObserver<String>() {

            @Override
            public void prepareCase(final int index, final String caseRaw)
            {
                final CaseFixture<String> caseFix =
                        Mockito.mock(CaseFixture.class);
                Mockito
                    .doReturn(new Rule("Visible:!Blind"))
                    .when(caseFix)
                    .getRule();

                setTransientValue(Trans.CaseFixture, caseFix);
            }
        });

        scenSrc.addObserver(
            Variable.parse_exception,
            new CaseObserver<String>() {

                @Override
                public void prepareCase(final int index, final String caseRaw)
                {
                    Mockito
                        .doThrow(new RuleEvaluatorException("Mock parse error"))
                        .when(mockRuleEval)
                        .parse(Matchers.anyString());
                }
            });

        scenSrc.addObserver(
            Variable.evaluate_exception,
            new CaseObserver<String>() {

                @Override
                public void prepareCase(final int index, final String caseRaw)
                {
                    Mockito
                        .doThrow(
                            new RuleEvaluatorException("Mock evaluate error"))
                        .when(mockRuleEval)
                        .parse(Matchers.anyString());
                }
            });

        scenSrc.addObserver(Variable.evaluate_ok, new CaseObserver<String>() {

            @Override
            public void prepareCase(final int index, final String caseRaw)
            {
                Mockito
                    .doReturn(true)
                    .when(mockRuleEval)
                    .evaluate((List<String>) Matchers.any(), Matchers.anyMap());
            }
        });

        scenSrc.notifyObservers();
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    @Override
    protected void execute()
    {
        try {
            getMockSubject().evaluate(
                (List<String>) getTransientValue(Trans.Scenario),
                (CaseFixture<String>) getTransientValue(Trans.CaseFixture));

            setResult("Good");

        } catch (final IllegalArgumentException iae) {
            setResult("Exception");
        } catch (final RuleEvaluatorException ex) {
            setResult("Error");
        }
    }

    /** */
    enum Trans {
        /** */
        Scenario, CaseFixture;
    }


}