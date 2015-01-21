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
package junitcast;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

/**
 * Base class for test case. JUnit4 version.
 *
 * PEA.
 *
 * Preparation. Execution(and Conversion). Assertion(or Verification).
 *
 * @author Royce Remulla.
 *
 * @param <T> Test Object instance type. Does not support generic types, you can
 *            omit the generic argument of test subject type.
 * @param <E> data type of scenario element. Use object if scenario contain
 *            multiple types.
 */
@RunWith(Parameterized.class)
public abstract class AbstractTestCase<T, E> {


    /** Parameterized runner composite parameter. */
    private final transient Parameter<E> parameter;

    /** Test class type to be derived as parameter type of test subclass. */
    private Class<T> subjectType;

    /** Mock object instance to test. */
    private transient T mockSubject;

    /** Internal. Real object instance. */
    private transient T realSubject;

    /** Optional result place holder bean property. */
    private transient Object result;


    /**
     * @param pParameter data transfer object to be injected by Parameterized
     *            test runner.
     */
    @SuppressWarnings("unchecked")
    protected AbstractTestCase(final Parameter<E> pParameter) {
        this.parameter = pParameter;
        if (getClass().getGenericSuperclass() instanceof ParameterizedType) {
            final ParameterizedType paramedType = (ParameterizedType) getClass()
                    .getGenericSuperclass();
            Class<T> subjecType;
            final Type type = paramedType.getActualTypeArguments()[0];
            if (type instanceof ParameterizedType) {
                subjecType = (Class<T>) ((ParameterizedType) type).getRawType();
            } else {
                subjecType = (Class<T>) paramedType.getActualTypeArguments()[0];
            }
            this.setSubjectType(subjecType);
        } else {
            throw new UnsupportedOperationException(
                    "Must use parameterized sub type.");
        }
    }

    /** JUnit 3 setUp(). */
    @Before
    public void setUp()
    {



        setupTargetObject(null);
    }

    /** JUnit 3 tearDown(). */
    @After
    public void tearDown()
    {
        setMockSubject(null);
        setRealSubject(null);
        setSubjectType(null);
    }


    /**
     * Setup the test object instance. Override this for custom implementation.
     *
     * @param constructorParams test subject constructor parameters.
     */
    protected abstract void setupTargetObject(List<Object> constructorParams);


    /** Preparation. */
    protected abstract void prepare();

    /**
     * Execution. Result output and exception must be converted to a valid
     * output token to be processed during assertion/verification.
     *
     * @return output matching the rule in the property file.
     */
    protected abstract Object execute();

    /**
     * Assertion/Verification.
     *
     * @param pResult execution result.
     */
    protected void assertVerify(final Object pResult)
    {
        String resultString;
        if (pResult == null) {
            resultString = "null";
        } else {
            resultString = pResult.toString();
        }

        Assert.assertEquals(getParameter().toString(), getParameter()
            .getExpected(), resultString);
    }

    /**
     * Default test method to catch all scenario test.
     */
    @Test
    public void cast()
    {
        //Preparation.
        prepare();

        //Execution.
        final Object lresult = execute();

        //Assertion/Verification.
        assertVerify(lresult);
    }

    /**
     * @return the mock instance.
     */
    protected T getMockSubject()
    {
        return this.mockSubject;
    }

    /**
     * @param pMockSubject test subject instance.
     */
    protected void setMockSubject(final T pMockSubject)
    {
        this.mockSubject = pMockSubject;
    }

    /**
     * @return the realObject
     */
    public T getRealSubject()
    {
        return this.realSubject;
    }

    /**
     * @param realObject the realObject to set
     */
    protected void setRealSubject(final T realObject)
    {
        this.realSubject = realObject;
    }

    /**
     * @return the targetType
     */
    protected Class<T> getSubjectType()
    {
        return this.subjectType;
    }

    /**
     * @param pSubjectType the subjectType to set
     */
    private void setSubjectType(final Class<T> pSubjectType)
    {
        this.subjectType = pSubjectType;
    }

    public Parameter<E> getParameter()
    {
        return this.parameter;
    }

    /**
     * Transient place holder for execution result.
     */
    public Object getResult()
    {
        return this.result;
    }

    /**
     * @param pResult the result to set
     */
    public void setResult(final Object pResult)
    {
        this.result = pResult;
    }

}
