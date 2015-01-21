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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.mockito.Mockito;

/**
 * Helper class for mockito test subclasses. You need to reference Mockito
 * library 1.8+ if you want to subclass this.
 * 
 * @author Royce Remulla
 */
public class MockitoHelper {

    /**
     * @param <T> Test Object instance type. Does not support generic types, you
     *            can omit the generic argument of test subject type. Must not
     *            be null.
     * 
     * @param testCase Test case instance.
     * @param constructorParams test subject constructor parameters.
     */
    @SuppressWarnings("unchecked")
    public <T> void setupTargetObject(final AbstractTestCase<T, ?> testCase,
                                      final List<Object> constructorParams)
    {
        List<Object> params;
        if (constructorParams == null) {
            params = new ArrayList<Object>();
        } else {
            params = constructorParams;
        }

        try {

            Constructor<T> constructor;
            if (params.isEmpty()) {
                constructor = (Constructor<T>) testCase
                    .getSubjectType()
                    .getDeclaredConstructors()[0];

            } else {
                constructor = (Constructor<T>) findConstructor(
                    testCase.getSubjectType(),
                    constructorParams);
            }

            if (constructor == null) {
                throw new JUnitCastException(
                    "Unable to autoresolve constructor based on the given arguments: "
                            + constructorParams);
            } else {

                constructor.setAccessible(true);
                T realSubject;
                if (constructorParams == null) {
                    realSubject = constructor.newInstance(new Object[0]);
                } else {
                    realSubject = constructor.newInstance(constructorParams
                        .toArray(new Object[constructorParams.size()]));
                }
                testCase.setRealSubject(realSubject);
                testCase.setMockSubject(Mockito.spy(testCase.getRealSubject()));
            }
        } catch (final InvocationTargetException ite) {
            throw new JUnitCastException(ite.getCause()); //NOPMD: we are concerned only on the source.
        } catch (final IllegalArgumentException e) {
            throw new JUnitCastException(e);
        } catch (final InstantiationException e) {
            throw new JUnitCastException(e);
        } catch (final IllegalAccessException e) {
            throw new JUnitCastException(e);
        }
    }

    /**
     * Auto resolve constructor based on List of Object parameter.
     * 
     * @param klazz class to derive constructor from.
     * @param pParams List of Object parameters.
     */
    Constructor<?> findConstructor(final Class<?> klazz,
                                   final List<Object> pParams)
    {

        @SuppressWarnings("rawtypes")
        final List<Constructor> matched = new ArrayList<Constructor>();
        for (final Constructor<?> nextConst : klazz.getDeclaredConstructors()) {
            if (nextConst.getParameterTypes().length == pParams.size()) {
                matched.add(nextConst);
                final List<Class<?>> parmTypes = getParamTypes(pParams);
                if (Arrays.asList(nextConst.getParameterTypes()).containsAll(
                    parmTypes)
                        && !matched.contains(nextConst)) {
                    matched.add(nextConst);
                }
            }
        }
        if (matched.size() == 1) {
            return matched.get(0);
        } else {
            throw new JUnitCastException("Constructor could not be resolved.");
        }
    }

    /**
     * List of object to list of class.
     * 
     * @param objects objects to convert.
     */
    List<Class<?>> getParamTypes(final List<Object> objects)
    {
        final List<Class<?>> retval = new ArrayList<Class<?>>();
        for (final Object object : objects) {
            if (object == null) {
                retval.add(null);
            } else {
                retval.add(object.getClass());
            }

        }
        return retval;
    }


    /**
     * Reset with null check.
     * 
     * @param mocks varargs mocks with null checking before reset.
     */
    public void reset(final Object... mocks)
    {
        for (final Object mock : mocks) {
            if (mock != null) {
                Mockito.reset(mock);
            }

        }
    }

}
