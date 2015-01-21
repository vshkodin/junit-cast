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

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;

/**
 * Default implementation of TransientValue interface.
 * 
 * @param <T> Test Object instance type. subject type.
 * @param <S> data type of scenario element.
 * @param <V> transient value element type. Object is commonly used.
 * 
 * @author Royce Remulla
 */
public abstract class AbstractTransientValueTestCase<T, S, V> extends
        AbstractTestCase<T, S> implements TransientValue<V> {

    /** Case objects place holder. */
    private transient Map<Object, V> transientValue;

    /**
     * @param pParameter Data Transfer Object Parameter in Parameterized test..
     * @see {@link AbstractTestCase#AbstractTestCase(Parameter)}.
     */
    public AbstractTransientValueTestCase(final Parameter<S> pParameter) {
        super(pParameter);
    }

    /**
     * JUnit 3 setUp().
     * 
     * Note: transient Map is internally used thus it need to be initialized
     * immediately before other setups.
     */
    @Override
    @SuppressWarnings({
            "unchecked",
            "rawtypes" })
    @Before
    public void setUp()
    {
        this.setTransientValue(new HashMap());
        super.setUp();
    }

    /** JUnit 3 tearDown(). */
    @Override
    @After
    public void tearDown()
    {
        super.tearDown();
        this.setTransientValue(null);
    }

    /** {@inheritDoc} */
    public V getTransientValue(final Object key)
    {
        return this.transientValue.get(key);
    }

    /** {@inheritDoc} */
    public void setTransientValue(final Object key, final V pValue)
    {
        this.transientValue.put(key, pValue);

    }

    private void setTransientValue(final Map<Object, V> pTransientValue)
    {
        this.transientValue = pTransientValue;
    }

}