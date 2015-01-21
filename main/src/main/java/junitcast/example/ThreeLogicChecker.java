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
public class ThreeLogicChecker {

    /**
     * Applies logical first or second and third. This will evaluate as (first || (second && third)).
     * 
     * @param first rtfc.
     * @param second rtfc.
     * @param third rtfc.
     */
    public boolean evaluate(final boolean first, final boolean second, final boolean third)
    {
        return first || second && third;
    }


}