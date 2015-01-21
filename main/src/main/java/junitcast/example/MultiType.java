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
public class MultiType {

    /** Position being applied for. */
    public enum Position {
        /** */
        Special(10), Regular(7);

        /** Minimum passing grade. */
        private final double minPassGrade;

        /**
         * @param pMinPassGrade minimum passing grade for the position.
         */
        Position(final double pMinPassGrade) {
            this.minPassGrade = pMinPassGrade;
        }

        double getMinimumGrade()
        {
            return this.minPassGrade;
        }

    }

    /**
     * 
     * @param position Position being applied for.
     * @param grade from 1 to 10.
     */
    public void applyForJob(final Position position, final double grade)
    {

        if (position == Position.Special) {
            if (grade == Position.Special.getMinimumGrade()) {
                recruit();
            } else {
                reject();
            }
        } else if (position == Position.Regular) {
            if (grade >= Position.Regular.getMinimumGrade()) {
                recruit();
            } else {
                reject();
            }
        }

    }

    /** reject job application. */
    void reject()
    {
        //used to demo testing of unimplemented dependency.
    }

    /** accept job application. */
    void recruit()
    {
        //used to demo testing of unimplemented dependency.        
    }

}
