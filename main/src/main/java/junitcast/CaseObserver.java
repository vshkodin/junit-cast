/**
 *   Copyright 2014 Royce Remulla
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

import junitcast.ann.Revision;

/**
 *
 * <pre>
 * @author $Author$
 * @version $Date$
 * </pre>
 *
 * @param <S> scenario parameter type. Normally String.
 */
@Revision("$Revision: $")
public interface CaseObserver<S> {

    /**
     * Case specific scenario processor.
     *
     * @param index scenario token index. Useful when you have similarly named
     *            tokens.
     * @param caseRaw raw case string defined in properties file.
     */
    void prepareCase(int index, S caseRaw);


}