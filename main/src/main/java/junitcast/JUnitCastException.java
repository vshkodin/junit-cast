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

/**
 *
 * <pre>
 * @author $Author$
 * @version $Date$
 * </pre>
 */

public class JUnitCastException extends RuntimeException {


    /**
     * Copied from JDK javadoc.
     *
     * @param cause the cause (which is saved for later retrieval by the
     *            Throwable.getCause() method). (A null value is permitted, and
     *            indicates that the cause is nonexistent or unknown.)
     */
    public JUnitCastException(final Throwable cause) {
        super(cause);
    }


    /**
     * Copied from JDK javadoc.
     *
     * @param message message - the detail message. The detail message is saved
     *            for later retrieval by the Throwable.getMessage() method.
     */
    public JUnitCastException(final String message) {
        super(message);
    }

}