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
 *
 *  Derived from: BracerParser - Class for parsing and evaluating math expressions.
 *  @author Dmytro Titov, @version 0.6.0, @since 0.1.0
 */
package junitcast.rule;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junitcast.converter.BoolConverter;
import junitcast.converter.ElementConverter;
import junitcast.converter.IntConverter;
import junitcast.converter.StrConverter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class for parsing and evaluating simple String rule against a List of String
 * match.
 *
 * @param <T> scenario element type.
 */
@SuppressWarnings("PMD.GodClass")
public class RuleEvaluator<T> {


    /** */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(RuleEvaluator.class);


    /** Custom logical AND/OR evaluator. */
    private final transient LogicHelper logicHelper = new LogicHelper();

    /** list of available operators. */
    private static final String OPERATORS = Operator.Not
            + String.valueOf(Operator.Or) + Operator.And; //NOPMD: This is not a String.

    /** temporary stack that holds operators, functions and brackets. */
    private final transient Stack<String> stackOperations = new Stack<String>();

    /** stack for holding expression converted to reversed polish notation. */
    private final transient Stack<String> stackRPN = new Stack<String>();

    /** stack for holding the calculations result. */
    private final transient Stack<String> stackAnswer = new Stack<String>();


    /** Rule token converter. */
    private final transient List<ElementConverter> converterList;


    /** */
    private static Map<Class<? extends Object>, ElementConverter> defaultConvertMap = new HashMap<Class<? extends Object>, ElementConverter>();
    static {
        defaultConvertMap.put(Integer.class, new IntConverter());
        defaultConvertMap.put(Boolean.class, new BoolConverter());
        defaultConvertMap.put(String.class, new StrConverter());
    }


    /** */
    enum Operator {
        /** */
        Not('!', Byte.MAX_VALUE), And('&', (byte) 2), Or('|', (byte) 1);

        /** */
        private final char operator;

        /** */
        private final byte precedence;

        /**
         *
         * @param pOperator operator symbol character.
         * @param pPrecedence precedence for expression evaluation.
         */
        Operator(final char pOperator, final byte pPrecedence) {
            this.operator = pOperator;
            this.precedence = pPrecedence;
        }

        /** String representation of this object. */
        @Override
        public String toString()
        {
            return String.valueOf(this.operator);
        }

        /**
         * Derive Operator instance from a given character.
         *
         * @param operator operator character symbol.
         */
        public static Operator fromChar(final char operator)
        {
            Operator retval = null; //NOPMD: null default, conditionally redefine.
            for (final Operator nextOper : Operator.values()) {
                if (operator == nextOper.operator) {
                    retval = nextOper;
                    break;
                }
            }
            return retval;
        }

        /**
         * @return the precedence
         */
        public byte getPrecedence()
        {
            return this.precedence;
        }
    }


    /** @param pConverterList list of rule token converters. */
    public RuleEvaluator(final List<ElementConverter> pConverterList) {
        this.converterList = pConverterList;
    }

    /**
     * Parses the math expression (complicated formula) and stores the result.
     *
     * @param pExpression <code>String</code> input expression (logical
     *            expression formula)
     * @since 0.3.0
     */
    public void parse(final String pExpression)
    {
        /* cleaning stacks */
        this.stackOperations.clear();
        this.stackRPN.clear();

        final String expression = pExpression;

        /* splitting input string into tokens */
        final StringTokenizer stringTokenizer = new StringTokenizer(
            expression,
            RuleEvaluator.OPERATORS + "()",
            true);


        /* loop for handling each token - shunting-yard algorithm */
        while (stringTokenizer.hasMoreTokens()) {
            final String token = stringTokenizer.nextToken().trim();
            doShuntInternal(token);
        }


        while (!this.stackOperations.empty()) {
            this.stackRPN.push(this.stackOperations.pop());
        }

        Collections.reverse(this.stackRPN);
    }

    /** @param token token. */
    private void doShuntInternal(final String token)
    {
        if (getLogicHelper().isOpenBracket(token)) {
            this.stackOperations.push(token);
        } else if (getLogicHelper().isCloseBracket(token)) {
            while (!this.stackOperations.empty()
                    && !getLogicHelper().isOpenBracket(
                        this.stackOperations.lastElement().trim())) {
                this.stackRPN.push(this.stackOperations.pop());
            }
            this.stackOperations.pop();
        } else if (isOperator(token)) {
            while (!this.stackOperations.empty()
                    && isOperator(this.stackOperations.lastElement().trim())
                    && getPrecedence(token.charAt(0)) <= getPrecedence(this.stackOperations
                        .lastElement()
                        .trim()
                        .charAt(0))) {
                this.stackRPN.push(this.stackOperations.pop());
            }
            this.stackOperations.push(token);
        } else {
            this.stackRPN.push(token);
        }
    }

    /**
     * Evaluates once parsed math expression with "var" variable included.
     *
     * @param scenario List of values to evaluate against the rule expression.
     * @param ruleTokenConvert mapping of rule tokens to converter.
     * @return <code>String</code> representation of the result
     */
    public Boolean evaluate(final List<T> scenario,
            final Map<String, ElementConverter> ruleTokenConvert)
    {
        Boolean retval;

        /* check if is there something to evaluate */
        if (this.stackRPN.empty()) {
            retval = Boolean.TRUE;
        } else if (this.stackRPN.size() == 1) {
            retval = evaluateOneRpn(scenario);
        } else {
            retval = evaluateMultiRpn(scenario, ruleTokenConvert);
        }
        return retval;

    }

    /**
     * @param scenario List of values to evaluate against the rule expression.
     * @param ruleTokenConvert token to converter map.
     */
    private Boolean evaluateMultiRpn(final List<T> scenario,
            final Map<String, ElementConverter> ruleTokenConvert)
    {
        /* clean answer stack */
        this.stackAnswer.clear();

        /* get the clone of the RPN stack for further evaluating */
        @SuppressWarnings("unchecked")
        final Stack<String> stackRPNClone = (Stack<String>) this.stackRPN
        .clone();


        /* evaluating the RPN expression */
        while (!stackRPNClone.empty()) {
            final String token = stackRPNClone.pop().trim();
            if (isOperator(token)) {
                if (Operator.Not.toString().equals(token)) {
                    evaluateMultiNot(scenario);
                } else {
                    evaluateMulti(
                        scenario,
                        ruleTokenConvert,
                        Operator.fromChar(token.charAt(0)));
                }
            } else {
                this.stackAnswer.push(token);
            }
        }

        if (this.stackAnswer.size() > 1) {
            throw new RuleEvaluatorException("Some operator is missing");
        }

        return Boolean.valueOf(this.stackAnswer.pop().substring(1));
    }

    /**
     * @param scenario List of values to evaluate against the rule expression.
     * @param ruleTokenConvert token to converter map.
     * @param operator OR/AND.
     */
    private void evaluateMulti(final List<T> scenario,
            final Map<String, ElementConverter> ruleTokenConvert,
            final Operator operator)
    {
        final ElementConverter defaultConverter = defaultConvertMap
                .get(scenario.get(0).getClass());
        final Object[] leftArr = getNextValue(
            ruleTokenConvert,
            defaultConverter);
        final Object[] rightArr = getNextValue(
            ruleTokenConvert,
            defaultConverter);

        Method method;
        try {
            method = LogicHelper.class.getDeclaredMethod("performLogical"
                    + operator.name(), new Class[] {
                        List.class,
                        Integer.TYPE,
                        Integer.TYPE,
                        Object.class,
                        Object.class });

            final String answer = (String) method.invoke(
                getLogicHelper(),
                new Object[] {
                    scenario,
                    (Integer) leftArr[0],
                    (Integer) rightArr[0],
                    leftArr[1],
                    rightArr[1] });

            if (answer.charAt(0) == '|') {
                this.stackAnswer.push(answer);
            } else {
                this.stackAnswer.push("|" + answer);
            }

        } catch (final SecurityException e1) {
            RuleEvaluator.LOGGER.error(e1.getMessage(), e1);
        } catch (final NoSuchMethodException e1) {
            RuleEvaluator.LOGGER.error(e1.getMessage(), e1);
        } catch (final IllegalArgumentException e) {
            RuleEvaluator.LOGGER.error(e.getMessage(), e);
        } catch (final IllegalAccessException e) {
            RuleEvaluator.LOGGER.error(e.getMessage(), e);
        } catch (final InvocationTargetException e) {
            RuleEvaluator.LOGGER.error(e.getMessage(), e);
        }

    }

    /**
     * @param scenario List of values to evaluate against the rule expression.
     */
    private void evaluateMultiNot(final List<T> scenario)
    {
        String answer;
        final String left = this.stackAnswer.pop().trim();
        if (LogicHelper.TRUE.equals(left)) {
            answer = LogicHelper.FALSE;
        } else if (LogicHelper.FALSE.equals(left)) {
            answer = LogicHelper.TRUE;
        } else {
            final int subscript = getSubscript(left);
            if (subscript > -1) {
                final ElementConverter defaultConverter = defaultConvertMap
                        .get(scenario.get(0).getClass());
                answer = String.valueOf(!scenario.get(subscript).equals(
                    defaultConverter.convert(left.substring(
                        0,
                        left.indexOf('[')))));
            } else {
                answer = String.valueOf(!scenario.contains(left));
            }
        }

        if (answer.charAt(0) == '|') {
            this.stackAnswer.push(answer);
        } else {
            this.stackAnswer.push('|' + answer);
        }
    }

    /** @param scenario List of values to evaluate against the rule expression. */
    private Boolean evaluateOneRpn(final List<T> scenario)
    {
        Boolean retval;
        final String single = this.stackRPN.peek();
        final int subscript = getSubscript(single);
        if (subscript > -1) {
            final ElementConverter defaultConverter = defaultConvertMap
                    .get(scenario.get(0).getClass());
            retval = scenario.get(subscript).equals(
                defaultConverter.convert(single.substring(
                    0,
                    single.indexOf('['))));
        } else {
            retval = scenario.contains(single);
        }
        return retval;
    }

    /**
     * @param ruleTokenConvert token to converter map.
     * @param defaultConverter default converter to use.
     */
    Object[] getNextValue(final Map<String, ElementConverter> ruleTokenConvert,
            final ElementConverter defaultConverter)
    {
        int subscript = -1; //NOPMD: -1 default, conditionally redefine.
        final List<Object> retval = new ArrayList<Object>();
        Object value = this.stackAnswer.pop().trim();
        if (!LogicHelper.TRUE.equals(value) && !LogicHelper.FALSE.equals(value)) {
            subscript = getSubscript(value.toString());
            final String valueStr = ((String) value).trim();
            if (subscript > -1) {
                final ElementConverter converter = this.converterList
                        .get(subscript);
                value = converter.convert(valueStr.substring(
                    0,
                    valueStr.indexOf('[')));
            } else {
                if (ruleTokenConvert == null
                        || ruleTokenConvert.get(valueStr) == null) {
                    value = defaultConverter.convert(valueStr);
                } else {
                    value = ruleTokenConvert.get(valueStr).convert(valueStr);
                }
            }
        }
        retval.add(subscript);
        retval.add(value);
        return retval.toArray(new Object[2]);
    }

    /**
     * Returns value of 'n' if rule token ends with '[n]'. where 'n' is the
     * variable group index.
     *
     * @param string token to check for subscript.
     */
    int getSubscript(final String string)
    {
        int retval = -1; //NOPMD: null default, conditionally redefine.
        final Pattern pattern = Pattern.compile(".*\\[[\\d*]\\]");
        final Matcher matcher = pattern.matcher(string);
        if (matcher.find()) {
            final String indexStr = string.substring(
                string.indexOf('[') + 1,
                string.indexOf(']'));
            retval = Integer.parseInt(indexStr);
        }
        return retval;
    }


    /**
     * Check if the token is operator.
     *
     * @param token Input <code>String</code> token
     * @return <code>boolean</code> output
     */
    private boolean isOperator(final String token)
    {
        return !"".equals(token) && RuleEvaluator.OPERATORS.contains(token);
    }

    /**
     * Gets the precedence of the operator.
     *
     * @param token Input <code>String</code> token
     * @return <code>byte</code> precedence
     * @since 0.1.0
     */
    byte getPrecedence(final char token)
    {
        return Operator.fromChar(token).getPrecedence();
    }

    public LogicHelper getLogicHelper()
    {
        return this.logicHelper;
    }

}


/** Specialized exception for parse error. */
class RuleEvaluatorException extends RuntimeException {

    /** @param string exception message. */
    RuleEvaluatorException(final String string) {
        super(string);
    }
}
