/**
 *   Copyright 2018 Royce Remulla
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

import junitcast.AbstractTestCase;
import junitcast.MockitoHelper;
import junitcast.Parameter;
import junitcast.ParameterGenerator;

import org.junit.runners.Parameterized.Parameters;
import org.mockito.Mockito;

/**
 * Test class for TODO.
 *
 * @author Royce Remulla
 */
public class TODOTest extends AbstractTestCase<TODO, String> {

    /**
     * @param pParameter Data Transfer Object Parameter in Parameterized test.
     */
    public WorkerTest(final Parameter<String> pParameter) {
        super(pParameter);
    }

    /** {@inheritDoc} */
    @Override
    protected void setupTargetObject(final List<Object> constructorParams)
    {
        new MockitoHelper().setupTargetObject(this, constructorParams);
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
            .genVarData();
    }

    /** {@inheritDoc} */
    @Override
    protected void prepare()
    {
        for (final String scenarioToken : getParameter().getScenario()) {
        }
    }

    /** {@inheritDoc} */
    @Override
    protected void execute()
    {
    }
}