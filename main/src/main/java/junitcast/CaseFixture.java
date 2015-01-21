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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junitcast.converter.ElementConverter;
import junitcast.rule.Rule;

/**
 * @param <T> scenario tokens type.
 * 
 * @author Royce Remulla
 */
public class CaseFixture<T> {

    /** Case description. */
    private final transient String caseDesc;

    /** */
    private final transient List<Set<T>> variables;

    /** */
    private transient List<ElementConverter> converters;

    /** Rule token converter. */
    private transient Map<String, ElementConverter> ruleConv;

    /** */
    private final transient Rule rule;

    /** Action pair map for binary result fixture. */
    private final transient Map<String, String> pairMap = new HashMap<String, String>();

    /** Action pair reverse map for binary result fixture. */
    private final transient Map<String, String> reversePairMap = new HashMap<String, String>();

    /** Case identifier. */
    private transient List<String> caseId;

    /** Exempt rule. */
    private transient String exemptRule;

    /**
     * @param pCaseDesc case description.
     * @param pVariables case variables.
     * @param pRule source output to rule mapping. (e.g. OUTPUT:true|false).
     */
    public CaseFixture(final String pCaseDesc, final List<Set<T>> pVariables, final Rule pRule) {
        this.caseDesc = pCaseDesc;
        this.variables = pVariables;
        this.rule = pRule;
        this.caseId = Arrays.asList(new String[] { pCaseDesc });
    }

    /**
     * @param pCaseDesc case description.
     * @param pVariables case variables.
     * @param pRule source output to rule mapping. (e.g. OUTPUT:true|false).
     * @param pPair output pair for binary output rules.
     */
    public CaseFixture(final String pCaseDesc, final List<Set<T>> pVariables, final Rule pRule, final String pPair) {
        this(pCaseDesc, pVariables, pRule);
        if (pPair != null) {
            final String[] pairArr = pPair.split(":");
            this.pairMap.put(pairArr[0], pairArr[1]);
            this.reversePairMap.put(pairArr[1], pairArr[0]);
        }
    }

    /**
     * Builder pattern.
     * 
     * @param pPair output pair for binary output rules.
     */
    public CaseFixture<T> pair(final String pPair)
    {
        if (pPair != null) {
            final String[] pairArr = pPair.split(":");

            this.pairMap.clear();
            this.reversePairMap.clear();

            this.pairMap.put(pairArr[0], pairArr[1]);
            this.reversePairMap.put(pairArr[1], pairArr[0]);
        }
        return this;
    }

    /**
     * Builder pattern.
     * 
     * @param pCaseId Case identifier.
     */
    public CaseFixture<T> caseIdentifier(final List<String> pCaseId)
    {
        this.caseId = pCaseId;
        return this;
    }

    /**
     * Builder pattern.
     * 
     * @param pExempt exemption rule.
     */
    public CaseFixture<T> exempt(final String pExempt)
    {
        this.exemptRule = pExempt;
        return this;
    }

    /**
     * Builder pattern.
     * 
     * @param pCaseId Case identifier.
     */
    public CaseFixture<T> caseIdentifier(final String pCaseId)
    {
        this.caseId = Arrays.asList(new String[] { pCaseId });
        return this;
    }

    /**
     * Builder pattern.
     * 
     * @param pConverters Element type converters.
     */
    public CaseFixture<T> convert(final List<ElementConverter> pConverters)
    {
        this.converters = pConverters;
        return this;
    }

    /**
     * Builder pattern.
     * 
     * @param pRuleConverter rule token converters.
     */
    public CaseFixture<T> ruleConverter(final Map<String, ElementConverter> pRuleConverter)
    {
        this.ruleConv = pRuleConverter;
        return this;
    }

    /**
     * @return the caseDesc
     */
    public String getCaseDesc()
    {
        return this.caseDesc;
    }

    /**
     * @return the rule
     */
    public Rule getRule()
    {
        return this.rule;
    }

    /**
     * @return the variables
     */
    public List<Set<T>> getVariables()
    {
        return this.variables;
    }

    /**
     * @return the pairMap
     */
    protected Map<String, String> getPairMap()
    {
        return this.pairMap;
    }

    /**
     * @return the reversePairMap
     */
    protected Map<String, String> getReversePairMap()
    {
        return this.reversePairMap;
    }

    /**
     * @return the caseId
     */
    public List<String> getCaseId()
    {
        return this.caseId;
    }

    public String getExemptRule()
    {
        return this.exemptRule;
    }

    public List<ElementConverter> getConverters()
    {
        return this.converters;
    }

    public void setConverters(final List<ElementConverter> pConverters)
    {
        this.converters = pConverters;
    }

    /**
     * @return the ruleConverter
     */
    public Map<String, ElementConverter> getRuleConverter()
    {
        return this.ruleConv;
    }

}