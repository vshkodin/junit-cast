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
package junitcast.example;

import java.util.Collection;
import java.util.List;

import junitcast.AbstractTransientValueTestCase;
import junitcast.Parameter;
import junitcast.ParameterGenerator;

import org.junit.runners.Parameterized.Parameters;

/** Test class for logic checker. */
public class LogicCheckerTest extends
        AbstractTransientValueTestCase<LogicChecker, Boolean, Boolean> {

    /** @param pParameter Data Transfer Object Parameter in Parameterized test. */
    public LogicCheckerTest(final Parameter<Boolean> pParameter) {
        super(pParameter);
    }

    /** */
    enum Argument {
        /** */
        first, second
    }

    @Override
    protected void setupTargetObject(final List<Object> constructorParams)
    {
        setMockSubject(new LogicChecker());
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
        return new ParameterGenerator<Integer>()
            .genVarData("junitcast.example.LogicCheckerTest");
    }

    @Override
    protected void prepare()
    {
        for (int i = 0; i < getParameter().getScenario().size(); i++) {
            setTransientValue(i, getParameter().getScenario().get(i));
        }
    }

    @Override
    protected Object execute()
    {
        return getMockSubject().and(
            getTransientValue(Argument.first.ordinal()),
            getTransientValue(Argument.second.ordinal()));
    }
}
