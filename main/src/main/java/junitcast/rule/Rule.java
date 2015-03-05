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
package junitcast.rule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import junitcast.util.StringUtil;

/**
 * Represents a logical rule.
 * 
 * <pre>
 * $Date$ 
 * $HeadURL$
 * </pre>
 * 
 * @author Royce Remulla
 */
public class Rule {

    /**
     * Map of action to List of rule clause.<br/>
     * <br/>
     * <b>Example:</b><br/>
     * 
     * Visible:Proposed|Approved<br/>
     * <br/>
     * Action = Visible.<br/>
     * Clause = Proposed|Approved<br/>
     * <br/>
     * 
     * Visible if arbitrary attribute is Proposed or Approved.
     */
    private final transient Map<String, String> actionRuleMap;

    /**
     * TODO: Validation of the pActRuleSrc parameter.
     * 
     * Instantiate a rule with the given clause. <br/>
     * <br/>
     * <b>Parameter Example:</b><br/>
     * Visible:Proposed|Approved<br/>
     * <br/>
     * Peace:Friendly|Indifferent\<br/>
     * ~War:Angry\<br/>
     * ~Neutral:Play safe
     * 
     * @param pActRuleSrc the Action and Rule clause String. Separated by
     *            colon(:). Multiple actions can be separated by tilde (~). <br/>
     */
    public Rule(final String pActRuleSrc) {
        final IllegalArgumentException exception = new IllegalArgumentException(
            "Invalid null action to rule source.");
        if (pActRuleSrc == null || pActRuleSrc.trim().endsWith(",")) {
            throw exception;
        }

        this.actionRuleMap = new LinkedHashMap<String, String>();
        final String[] ruleArr = pActRuleSrc.split("~");

        final List<String> duplicate = new ArrayList<String>();
        for (final String nextRule : ruleArr) {
            final String[] actionClauseArr = nextRule.split(":");
            if (ruleArr.length > 1
                    && (actionClauseArr.length < 2 || ""
                        .equals(actionClauseArr[1]))) {
                throw exception;
            }

            final String action = actionClauseArr[0].trim();
            if (duplicate.contains(action)) {
                throw exception;
            } else {
                duplicate.add(action);
            }

            String clause;
            if (actionClauseArr.length == 1) {
                clause = "";
            } else {
                clause = removeSpaces(actionClauseArr[1], "\\(");
                clause = removeSpaces(clause, "\\)");
                clause = removeSpaces(clause, "&");
                clause = removeSpaces(clause, "\\|");
                clause = removeSpaces(clause, "!");
            }
            this.actionRuleMap.put(action, clause.trim());
        }
    }

    /**
     * Removes the leading and trailing spaces of rule tokens.
     * 
     * @param string rule clause.
     * @param separator rule clause token.
     */
    final String removeSpaces(final String string, final String separator)
    {
        return string.replaceAll("\\s*" + separator + "\\s*", separator);
    }

    /**
     * @return the actionList
     */
    public List<String> getActionList()
    {
        return new ArrayList<String>(this.actionRuleMap.keySet());
    }

    /**
     * @param action action which rule we want to retrieve.
     * @return the actionToRuleClauses
     */
    public String getRuleClause(final String action)
    {
        return this.actionRuleMap.get(action);
    }

    /**
     * Get rule result give a fixed list of scenario tokens. Used for fixed
     * list.
     * 
     * @param scenario of interest.
     * @return the actionToRuleClauses
     */
    @SuppressWarnings("PMD.OnlyOneReturn" /* Two only. */)
    public String getRuleAction(final List<String> scenario)
    {
        assert scenario != null;

        final String scenStr = scenario.toString();
        final String andedScen = scenStr
            .substring(1, scenStr.length() - 1)
            .replaceAll(", ", "&");

        for (final String key : actionRuleMap.keySet()) {
            final String clause = actionRuleMap.get(key);

            final List<String> orListClause = Arrays.asList(StringUtil
                .trimArray(clause.split("\\|")));
            if (orListClause.contains(andedScen)) {
                return key;
            }
        }
        return null;
    }

    /**
     * @see {@link Object#toString()}
     * @return String representation of this instance.
     */
    @Override
    public String toString()
    {
        return String.valueOf(this.actionRuleMap);
    }

}