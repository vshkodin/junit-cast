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

//Subclass junitcast.AbstractTestCase.
//<Worker, String> refers to the subject class, data type of scenario.
public class WorkerBlogTest extends AbstractTestCase<Worker, String> {

    //Parameter is the scenario/stick data transfer object.
    public WorkerBlogTest(final Parameter<String> pParameter) {super(pParameter);}

    //This will instantiate the subject.
    @Override
    protected void setupTargetObject(final List<Object> constructorParams) {
        new MockitoHelper().setupTargetObject(this, constructorParams);
    }

    //Optional enum to reference to all the variables defined in the property file.
    enum Variable { Is_Holiday, Regular_Day, Sunday, Monday, Tuesday, Wednesday, Thursday, Friday, Saturday }

    //Optional enum to reference the possible outcomes defined in the property file.
    enum Result {
        Rest, Go_to_work;
        public String value() {
            return super.name().replaceAll("_", " ");
        }
    }


    //This is the parameter generator required by JUnit parameterized test runner.
    //This is where the popsicle sticks are constructed.
    @Parameters(name = "{0}")
    public static Collection<Object[]> generateData() {
        return new ParameterGenerator<String>().genVarData("junitcast.example.WorkerTest");
    }

    //Step 1 of the PEA pattern.
    @Override
    protected void prepare() {
        for (final String scenarioToken : getParameter().getScenario()) {
            final Variable variable = Variable.valueOf(scenarioToken.replaceAll(" ", "_"));
            switch (variable) {
                case Is_Holiday:
                    Mockito.doReturn(true).when(getMockSubject()).isHoliday();
                    break;
                case Regular_Day:
                    Mockito.doReturn(false).when(getMockSubject()).isHoliday(); //can be ommitted.
                    break;
                default:
                    final Worker.Day day = Worker.Day.valueOf(scenarioToken);
                    Mockito.doReturn(day).when(getMockSubject()).getDayOfTheWeek();
                    break;
            }
        }
    }

    //Step 2. of the PEA pattern.
    @Override
    protected Object execute() {
        if (getMockSubject().hasWork(null)) {
            setResult(Result.Go_to_work.value());
        } else {
            setResult(Result.Rest.value());
        }
        return getResult();
    }

    //Step 3 (Assertion) of the PEA pattern is taken care of internally by the super class.
}
