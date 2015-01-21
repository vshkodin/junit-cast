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

import java.util.List;

/**
 * Data Transfer Object Parameter in Parameterized test.
 *
 * @param <E> scenario element type.
 */
public class Parameter<E> {


    /** Case description. */
    private final transient String caseDesc;

    /** Test scenario. */
    private final transient List<E> scenario;

    /** Expected result, given the current test scenario. */
    private final transient String expected;

    /**
     * Used to uniquely identify test case. Optional identifier is used to
     * isolate test case for multiple cases in a test class.
     */
    private final transient List<String> identifier;


    /**
     * @param pCaseDesc case description, must not be null.
     * @param pScenario current scenario, must neither be null nor empty.
     * @param pExpected expected result, must not be null or empty.
     * @param pIdentifier Optional case identifier.
     */
    protected Parameter(final String pCaseDesc, final List<E> pScenario,
            final String pExpected, final List<String> pIdentifier) {
        super();

        if (pCaseDesc == null || "".equals(pCaseDesc.trim())) {
            throw new IllegalArgumentException("pExpected must not be null");
        }

        if (pScenario == null || pScenario.isEmpty()) {
            throw new IllegalArgumentException("scenario must not be null");
        }

        if (pExpected == null) {
            throw new IllegalArgumentException("pExpected must not be null");
        }

        this.caseDesc = pCaseDesc;

        this.scenario = pScenario;
        this.expected = pExpected;
        this.identifier = pIdentifier;
    }

    /**
     * @return the result
     */
    public String getExpected()
    {
        return this.expected;
    }

    /**
     * @return the identifier
     */
    public List<String> getIdentifier()
    {
        return this.identifier;
    }

    public List<E> getScenario()
    {
        return this.scenario;
    }

    /**
     * This will appear in the test runner grid.
     *
     * @see {@link Object#toString()}
     * @return String representation of this instance.
     */
    @Override
    public String toString()
    {
        final StringBuilder retval = new StringBuilder()
        .append(getCaseDesc())
        .append(": Expect=[")
        .append(getExpected())
        .append("], Scenario")
        .append(getScenario());

        return retval.toString();
    }

    /**
     * @return the caseDesc
     */
    public String getCaseDesc()
    {
        return this.caseDesc;
    }

}