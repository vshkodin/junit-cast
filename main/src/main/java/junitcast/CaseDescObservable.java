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

import java.util.HashMap;
import java.util.Map;

import junitcast.ann.Revision;

/**
 * Case Description observable.
 *
 * <pre>
 * @author $Author$
 * @version $Date$
 * </pre>
 *
 * @param <S> data type of scenario element. Use object if scenario contain
 *            multiple types.
 */
@Revision("$Revision: $")
public class CaseDescObservable<S> {


    /** */
    private final transient AbstractTestCase<?, S> testCase;

    /** */
    private final transient Map<String, AnswerObserver> enumObsMap =
            new HashMap<String, AnswerObserver>();


    /**
     * By convention, accessible Variable enum defined on the test class.
     *
     * @param pTestCase the test class usually "this". Not null.
     */
    public CaseDescObservable(final AbstractTestCase<?, S> pTestCase) {

        assert pTestCase != null;

        this.testCase = pTestCase;
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
    public void addObserver(final String pCaseDesc,
                            final AnswerObserver pObserver)
    {
        assert pObserver != null : "You cannot have a null observer.";

        this.enumObsMap.put(pCaseDesc, pObserver);
    }


    /** Notify all case observers. */
    public void notifyObservers()
    {

        final AnswerObserver ansObserver =
                this.enumObsMap.get(this.testCase.getParameter().getCaseDesc());
        if (ansObserver != null) {
            ansObserver.initAnswers();
        }

        this.enumObsMap.clear();
    }


    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        return getClass().getSimpleName() + "["
                + this.testCase.getClass().getSimpleName()
                + "] Observer size: " + this.enumObsMap.size();
    }

}