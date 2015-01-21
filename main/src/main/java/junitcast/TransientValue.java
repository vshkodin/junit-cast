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

/**
 * Provides facility to transfer test case values between test preparation and
 * test execution.
 * 
 * @param <V> transient value element type.
 * 
 * @author Royce Remulla
 */
public interface TransientValue<V> {

    /**
     * Get transient value from the given index.
     * 
     * @param key dictionary key.
     * @return the testParameter at the given index.
     */
    V getTransientValue(final Object key);

    /**
     * Set transient value on a given index.
     * 
     * @param key dictionary key.
     * @param pValue the testParameter to set
     */
    void setTransientValue(final Object key, final V pValue);

}