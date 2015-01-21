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

import junitcast.AbstractTestCase;
import junitcast.MockitoHelper;
import junitcast.Parameter;
import junitcast.ParameterGenerator;

import org.junit.runners.Parameterized.Parameters;
import org.mockito.Mockito;

/**
 * Test class for Worker.
 *
 * @author Royce Remulla
 */
public class WorkerTest extends AbstractTestCase<Worker, String> {

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

    /** */
    enum Variable {
        /** */
        Is_Holiday, Regular_Day, Sunday, Monday, Tuesday, Wednesday, Thursday,
        /** */
        Friday, Saturday
    }

    /** Outputs. */
    enum Result {

        /** */
        Rest,
        /** */
        Go_to_work;

        /** */
        public String value()
        {
            return super.name().replaceAll("_", " ");
        }
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
            .genVarData("junitcast.example.WorkerTest");
    }

    /** {@inheritDoc} */
    @Override
    protected void prepare()
    {
        for (final String scenarioToken : getParameter().getScenario()) {

            final Variable variable = Variable.valueOf(scenarioToken
                .replaceAll(" ", "_"));
            switch (variable) {
                case Is_Holiday:
                    Mockito.doReturn(true).when(getMockSubject()).isHoliday();
                    break;
                case Regular_Day:
                    Mockito.doReturn(false).when(getMockSubject()).isHoliday();
                    break;
                default:
                    final Worker.Day day = Worker.Day.valueOf(scenarioToken);
                    Mockito
                        .doReturn(day)
                        .when(getMockSubject())
                        .getDayOfTheWeek();
                    break;
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    protected Object execute()
    {
        if (getMockSubject().hasWork(null)) {
            setResult(Result.Go_to_work.value());
        } else {
            setResult(Result.Rest.value());
        }
        return getResult();
    }

}