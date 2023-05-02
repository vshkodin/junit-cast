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
package junitcast.tool;

/**
 * JUnitCast constants.
 * 
 * <pre>
 * $Author$ 
 * $Date$ 
 * $HeadURL$
 * </pre>
 * 
 * @author Royce Remulla
 */
public final class Constant {


    /** */
    public static final String TEST = "NOPMD";


    /** Utility class. */
    private Constant() {}


    /** */
    public static enum ResourceKey {
        /** */
        casedesc, caseid, var, commonvar, exempt, commonexempt, pair, rule
    }


    /** */
    public static enum VelocityField {

        /** */
        testname, classname, pkg("package"), var, varlist, kase("case"),

        /** */
        caselist, resultleft, resultright, resultlist, gendate;

        /** */
        private final transient String param;

        /** Use enum name as parameter. */
        VelocityField() {
            this.param = this.name();
        }

        /**
         * @param pParam actual name.
         */
        VelocityField(final String pParam) {
            this.param = pParam;
        }

        public String getParam()
        {
            return this.param;
        }


    }


}
