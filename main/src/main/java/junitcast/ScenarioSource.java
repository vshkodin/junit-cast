/**
 *   Copyright 2014 Royce Remulla
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junitcast.util.Constant;

/**
 * Scenario observable.
 *
 * <pre>
 * @author $Author$
 * @version $Date$
 * </pre>
 *
 * @param <S> data type of scenario element. Use object if scenario contain
 *            multiple types.
 */
public class ScenarioSource<S> {


    /** Internal source control version. */
    public static final String RCS_ID = "$Revision$";


    /** */
    private final transient AbstractTestCase<?, S> testCase;

    /** */
    @SuppressWarnings("rawtypes")
    private final transient Map<Enum, List<CaseObserver<S>>> map = new HashMap<Enum, List<CaseObserver<S>>>();

    /** */
    @SuppressWarnings("rawtypes")
    private final transient Class<? extends Enum> enumType;


    /**
     * By convention, accessible Variable enum defined on the test class.
     *
     * @param pTestCase the test class usually "this". Not null.
     */
    public ScenarioSource(final AbstractTestCase<?, S> pTestCase) {

        assert pTestCase != null;

        this.testCase = pTestCase;
        this.enumType = findVariableEnum(pTestCase);
    }


    /**
     * rtfc.
     *
     * @param pTestCase Not null.
     * @return null when Variable enum is not found.
     */
    @SuppressWarnings(Constant.Warning.UNCHECKED)
    Class<? extends Enum<?>> findVariableEnum(
            final AbstractTestCase<?, S> pTestCase)
    {

        Class<? extends Enum<?>> retval = null; //NOPMD: null default, conditionally redefine.
        for (final Class<?> innerClass : pTestCase
            .getClass()
            .getDeclaredClasses()) {
            if (innerClass.getSimpleName().startsWith("Var")) {
                retval = (Class<? extends Enum<?>>) innerClass;
                break;
            }
        }
        return retval;
    }


    /**
     * Convenience method to set transient value on a test case.
     * 
     * @param key transient key/name.
     * @param caseParser case parser instance. Must not be null.
     * @param cases applicable Variable cases.
     * 
     * @param <C> case enum.
     * @param <T> transient enum.
     */
    public <C extends Enum<C>, T extends Enum<T>> void addTransientCase(
            final T key, final CaseParser caseParser, final C... cases)
    {
        addTransientCase(key, (Object) caseParser, cases);
    }

    /**
     * Convenience method to set transient value on a test case using the case
     * name.
     * 
     * @param key transient key/name.
     * @param cases applicable Variable cases.
     * 
     * @param <C> case enum.
     * @param <T> transient enum.
     */
    public <C extends Enum<C>, T extends Enum<T>> void addTransientCaseName(
            final T key, final C... cases)
    {
        checkValidTestCase(cases);
        for (final C nextCase : cases) {
            addTransientCase(key, nextCase.name(), cases);
        }
    }


    /**
     * Convenience method to set transient value on a test case.
     * 
     * @param key transient key/name.
     * @param value transient value to set.
     * @param cases applicable Variable cases.
     * 
     * @param <C> case enum.
     * @param <T> transient enum.
     */
    public <C extends Enum<C>, T extends Enum<T>> void addTransientCase(
            final T key, final Object value, final C... cases)
    {
        assert cases != null;
        assert cases.length > 0;
        for (final C nextCase : cases) {
            addObserver(nextCase, createNewCase(nextCase, key, value));
        }
    }

    <T extends Enum<T>, C extends Enum<C>> CaseObserver<S> createNewCase(
            final C nextCase, final T key, final Object value)
    {
        return new CaseObserver<S>() {

            @Override
            public void prepareCase(final S caseRaw)
            {
                Object valueCalc;
                if (value instanceof CaseParser) {
                    final CaseParser caseParser = (CaseParser) value;
                    valueCalc = caseParser.parse(nextCase);
                } else {
                    valueCalc = value;
                }
                @SuppressWarnings(Constant.Warning.UNCHECKED)
                final AbstractTransientValueTestCase<?, S, Object> transCase = (AbstractTransientValueTestCase<?, S, Object>) testCase;
                transCase.setTransientValue(key, valueCalc);
            }
        };
    }


    /**
     * Helper method for #addTransientCase(). Checks if test case supports the
     * functionality.
     */
    <C extends Enum<C>> void checkValidTestCase(final C... cases)
    {
        if (cases == null || cases.length == 0) {
            throw new IllegalArgumentException(
                "Must have at least one valid case.");
        }


        if (!(this.testCase instanceof AbstractTransientValueTestCase)) {
            throw new UnsupportedOperationException(
                "Test case must be a sub class of "
                        + AbstractTransientValueTestCase.class.getSimpleName()
                        + " for this method to work.");
        }
    }


    /**
     *
     * @param kaso enum case.
     * @param observer case observer instance.
     */
    public void addObserver(final Enum<?> kaso, final CaseObserver<S> observer)
    {
        if (this.map.get(kaso) == null) {
            this.map.put(kaso, new ArrayList<CaseObserver<S>>());
        }
        if (observer != null) {
            final List<CaseObserver<S>> caseObsList = this.map.get(kaso);
            caseObsList.add(observer);
        }
    }


    /** Notify all case observers. */
    public void notifyObservers()
    {

        for (final S nextCase : this.testCase.getParameter().getScenario()) {

            @SuppressWarnings(Constant.Warning.UNCHECKED)
            final Enum<?> nextEnum = Enum.valueOf(this.enumType, nextCase
                .toString()
                .replaceAll(" ", ""));

            final List<CaseObserver<S>> caseObsList = this.map.get(nextEnum);

            if (caseObsList != null) {
                for (final CaseObserver<S> nextCaseObserver : caseObsList) {
                    nextCaseObserver.prepareCase(nextCase);
                }
            }
        }
    }


    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        return super.toString() + " " + RCS_ID;
    }

}