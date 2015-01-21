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
package junitcast.rule;

import java.util.List;


/**
 * Custom logical AND/OR evaluator.
 *
 * @author Royce
 */
public class LogicHelper {

    /** */
    static final String TRUE = "|" + true;

    /** */
    static final String FALSE = "|" + false;

    /**
     * @param scenario list of scenario tokens.
     * @param leftSubscript left index.
     * @param rightSubscript right index.
     * @param left left token.
     * @param right right token.
     */
    String performLogicalAnd(final List<?> scenario, final int leftSubscript,
            final int rightSubscript, final Object left, final Object right)
    {
        String answer;
        if (FALSE.equals(left) || FALSE.equals(right)) {
            answer = FALSE;
        } else if (TRUE.equals(left) && TRUE.equals(right)) {
            answer = TRUE;
        } else if (TRUE.equals(left)) {
            if (rightSubscript > -1) {
                answer = String.valueOf(scenario.get(rightSubscript).equals(
                    right));
            } else {
                answer = String.valueOf(scenario.contains(right));
            }
        } else if (TRUE.equals(right)) {

            if (leftSubscript > -1) {
                answer = String.valueOf(scenario
                    .get(leftSubscript)
                    .equals(left));
            } else {
                answer = String.valueOf(scenario.contains(left));
            }

        } else {

            final boolean leftEval = evaluate(scenario, leftSubscript, left);
            final boolean rightEval = evaluate(scenario, rightSubscript, right);
            answer = String.valueOf(leftEval && rightEval);
        }
        return answer;
    }


    /**
     * @param scenario list of scenario tokens.
     * @param leftSubscript left index.
     * @param rightSubscript right index.
     * @param left left token.
     * @param right right token.
     */
    String performLogicalOr(final List<?> scenario, final int leftSubscript,
            final int rightSubscript, final Object left, final Object right)
    {
        String answer;
        if (TRUE.equals(left) || TRUE.equals(right)) {
            answer = TRUE;
        } else if (FALSE.equals(left) && FALSE.equals(right)) {
            answer = FALSE;
        } else if (FALSE.equals(left)) {
            if (rightSubscript > -1) {
                answer = String.valueOf(scenario.get(rightSubscript).equals(
                    right));
            } else {
                answer = String.valueOf(scenario.contains(right));
            }
        } else if (FALSE.equals(right)) {
            if (leftSubscript > -1) {
                answer = String.valueOf(scenario
                    .get(leftSubscript)
                    .equals(left));
            } else {
                answer = String.valueOf(scenario.contains(left));
            }
        } else {

            final boolean leftEval = evaluate(scenario, leftSubscript, left);
            final boolean rightEval = evaluate(scenario, rightSubscript, right);
            answer = String.valueOf(leftEval || rightEval);
        }

        return answer;
    }

    /**
     * Helper method to evaluate left or right token.
     *
     * @param scenario list of scenario tokens.
     * @param subscript scenario token subscript.
     * @param object left or right token.
     */
    boolean evaluate(final List<?> scenario, final int subscript,
            final Object object)
    {
        boolean retval;
        if (subscript > -1) {
            retval = scenario.get(subscript).equals(object);
        } else {
            retval = scenario.contains(object);
        }
        return retval;
    }

    /**
     * Check if the token is opening bracket.
     *
     * @param token Input <code>String</code> token
     * @return <code>boolean</code> output
     */
    boolean isOpenBracket(final String token)
    {
        return "(".equals(token);
    }

    /**
     * Check if the token is closing bracket.
     *
     * @param token Input <code>String</code> token
     * @return <code>boolean</code> output
     */
    boolean isCloseBracket(final String token)
    {
        return ")".equals(token);
    }
}
