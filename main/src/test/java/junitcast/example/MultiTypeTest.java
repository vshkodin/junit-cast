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
package junitcast.example;

import java.util.Collection;
import java.util.List;

import junitcast.AbstractTransientValueTestCase;
import junitcast.MockitoHelper;
import junitcast.Parameter;
import junitcast.ParameterGenerator;
import junitcast.example.MultiType.Position;

import org.junit.runners.Parameterized.Parameters;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

/**
 * Test class for MultiType.
 */
public class MultiTypeTest extends
        AbstractTransientValueTestCase<MultiType, Object, Object> {


    /** @param pParameter Data Transfer Object Parameter in Parameterized test. */
    public MultiTypeTest(final Parameter<Object> pParameter) {
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
        return new ParameterGenerator<Object>()
            .genVarData("junitcast.example.MultiTypeTest");
    }

    /** {@inheritDoc} */
    @Override
    protected void prepare()
    {
        final String posStr = getParameter()
            .getScenario()
            .get(Argument.Position.ordinal())
            .toString();
        final Position position = Position.valueOf(posStr);
        final Double grade = (Double) getParameter().getScenario().get(
            Argument.Grade.ordinal());

        setTransientValue(Argument.Position.ordinal(), position);
        setTransientValue(Argument.Grade.ordinal(), grade);

        Mockito.doAnswer(new Answer<Object>() {
            @Override
            public Object answer(final InvocationOnMock invocation)
                    throws Throwable
            {
                setResult("ACCEPT");
                return null;
            }
        })
            .when(getMockSubject())
            .recruit();

        Mockito.doAnswer(new Answer<Object>() {
            @Override
            public Object answer(final InvocationOnMock invocation)
                    throws Throwable
            {
                setResult("DECLINE");
                return null;
            }
        })
            .when(getMockSubject())
            .reject();


    }

    /** {@inheritDoc} */
    @Override
    protected Object execute()
    {
        final MultiType.Position position = (Position) getTransientValue(Argument.Position
            .ordinal());
        final Double grade = (Double) getTransientValue(Argument.Grade
            .ordinal());
        getMockSubject().applyForJob(position, grade);
        return getResult();
    }

    /** */
    enum Argument {
        /** */
        Position, Grade
    }

}
