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
public class LogicChecker {

    /**
     * Perform logical AND operation on two arguments.
     * 
     * @param argument1 first argument.
     * @param argument2 second argument.
     */
    public boolean and(final boolean argument1, final boolean argument2)
    {
        return argument1 && argument2;
    }    
}
