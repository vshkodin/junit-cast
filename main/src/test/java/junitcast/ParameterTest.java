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
import java.util.List;

import org.junit.runners.Parameterized.Parameters;

/**
 * Test class for Parameter.
 *
 * <pre>
 * $Author$
 * $Date$
 * $HeadURL$
 * </pre>
 *
 * @author Royce Remulla
 */
public class ParameterTest extends
        AbstractTransientValueTestCase<Parameter<String>, String, Object> {

    /**
     * @param pParameter Data Transfer Object Parameter.
     */
    public ParameterTest(final Parameter<String> pParameter) {
        super(pParameter);
    }

    @Override
    protected void setupTargetObject(final List<Object> constructorParams)
    {
        prepare();
        try {
            new MockitoHelper().setupTargetObject(
                this,
                Arrays.asList(new Object[] {
                        getTransientValue(ConstructorParameter.CaseDesc),
                        getTransientValue(ConstructorParameter.Scenario),
                        getTransientValue(ConstructorParameter.Result),
                        getTransientValue(ConstructorParameter.Identifier) }));
        } catch (final JUnitCastException iae) {
            setResult("ERROR");
        }
    }

    /** Test subject constructor parameter indices. */
    enum ConstructorParameter {
        /** */
        CaseDesc,
        /** */
        Scenario,
        /** */
        Result,
        /** */
        Identifier
    }

    /** List of variables that affect the test subject. */
    enum Variable {

        /** */
        null_case_desc,
        /** */
        has_case_desc,
        /** */
        null_scenario,

        /** Empty scenario set. */
        empty_scenario,

        /** One element in scenario. */
        one_scenario,

        /** Three element in scenario. */
        three_scenario,

        /** */
        null_result,

        /** */
        has_result,

        /** */
        null_id,

        /** */
        has_id
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
            .genVarData("junitcast.ParameterTest");
    }

    /** {@inheritDoc} */
    @Override
    protected void prepare()
    {
        final ScenarioSource<String> source = new ScenarioSource<String>(this);

        source.addTransientCaseName(
            ConstructorParameter.CaseDesc,
            Variable.has_case_desc);

        source.addTransientCase(
            ConstructorParameter.Scenario,
            new ArrayList<String>(),
            Variable.empty_scenario);

        source.addTransientCase(
            ConstructorParameter.Scenario,
            Arrays.asList(new String[] { "scen1" }),
            Variable.one_scenario);

        source.addTransientCase(
            ConstructorParameter.Scenario,
            Arrays.asList(new String[] {
                    "scen1",
                    "scen2",
                    "scen3" }),
            Variable.three_scenario);

        source.addTransientCase(
            ConstructorParameter.Result,
            Variable.has_result + "1",
            Variable.has_result);

        source.addTransientCase(
            ConstructorParameter.Identifier,
            Arrays.asList(new String[] { Variable.has_id + "2" }),
            Variable.has_id);

        source.notifyObservers();
    }


    /** {@inheritDoc} */
    @Override
    protected Object execute()
    {
        if (getMockSubject() == null) {
            setResult("ERROR");
        } else {
            setResult("GOOD");
        }
        return getResult();
    }

}