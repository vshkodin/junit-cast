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
package junitcast.example;

/**
 * @author Royce Remulla
 */
public class SimpleDivider {

    /** */
    private static final double EPSILON = 1.11e-16;

    /**
     * Disallow 0 divisor. Return quotient.
     * 
     * @param dividend any integer from Integer.MIN_VALUE to Integer.MAX_VALUE.
     * @param divisor must not be 0.
     */
    public double divide(final int dividend, final int divisor)
    {
        if (divisor == 0) {
            throw new IllegalArgumentException("Invalid divisor.");
        } else {
            double retval = (double) dividend / (double) divisor;
            if (Math.abs(retval) < EPSILON) { //prevent negative zero.
                retval = 0.0;
            }
            return retval;
        }
    }
}
