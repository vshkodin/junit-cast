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
package junitcast.util;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import junitcast.AbstractTransientValueTestCase;
import junitcast.MockitoHelper;
import junitcast.Parameter;
import junitcast.ParameterGenerator;
import junitcast.ScenarioSource;

import org.junit.Assert;
import org.junit.runners.Parameterized.Parameters;

/**
 * Test class for StringUtil.
 * 
 * @author Royce Remulla
 */
public class StringUtilTest extends
        AbstractTransientValueTestCase<StringUtil, String, String[]> {

    /** @param pParameter Data Transfer Object Parameter in Parameterized test. */
    public StringUtilTest(final Parameter<String> pParameter) {
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
        null_array, empty, normal, null_item
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
            .genVarData("junitcast.util.StringUtilTest");
    }

    @Override
    protected Object execute()
    {
        Object retval = null; //NOPMD: null default, conditionally redefine.
        try {
            final Object result = getRealSubject()
                .getClass()
                .getDeclaredMethod(
                    getParameter().getIdentifier().get(0),
                    new Class[] { String[].class })
                .invoke(getMockSubject(), new Object[] { getTransientValue(0) });
            if (result == null) {
                retval = "null";
            } else {
                final Object[] arr = (Object[]) result;
                if (arr.length == 0) {
                    retval = "empty";
                } else {
                    retval = Arrays
                        .asList((String[]) result)
                        .toString()
                        .replaceAll(", ", "-");
                }
            }
        } catch (final IllegalArgumentException e) {
            Assert.fail(e.getMessage());
        } catch (final SecurityException e) {
            Assert.fail(e.getMessage());
        } catch (final IllegalAccessException e) {
            Assert.fail(e.getMessage());
        } catch (final InvocationTargetException e) {
            Assert.fail(e.getMessage());
        } catch (final NoSuchMethodException e) {
            Assert.fail(e.getMessage());
        }
        return retval;
    }

    /** {@inheritDoc} */
    @Override
    protected void prepare()
    {
        final ScenarioSource<String> source = new ScenarioSource<String>(this);

        source.addTransientCase(0, null, Variable.null_array);
        source.addTransientCase(0, new String[0], Variable.empty);

        source.addTransientCase(0, new String[] {
                " a",
                " b ",
                "         c                       ",
                "d" }, Variable.normal);

        source.addTransientCase(0, new String[] {
                " a",
                "b ",
                " c                       ",
                null }, Variable.null_item);

        source.notifyObservers();
    }

}
