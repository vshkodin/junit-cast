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
 * Checks if a number is prime number or not.
 * 
 * @author r39
 */
public class PrimeNumber {

    /**
     * Returns true if input is positive and is a prime number.
     * 
     * @param input integer input.
     */
    public boolean isPrimeNumber(final int input)
    {
        boolean primeYes = true; //NOPMD: true default, conditionally redefine.
        if (input <= 0) {
            throw new IllegalArgumentException("Invalid number: " + input);
        } else if (input > 2) {

            for (int i = 2; i < input; i++) {
                primeYes = input % i != 0;
                if (!primeYes) {
                    break;
                }
            }
        }
        return primeYes;
    }

}