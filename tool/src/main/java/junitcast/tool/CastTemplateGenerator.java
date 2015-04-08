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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ph.rye.common.io.ConsoleInputReader;
import ph.rye.common.lang.StringUtil;


/**
 * Use to generate template test case file based on JUnitCast property file.
 *
 * <pre>
 * $Author$
 * $Date$
 * $HeadURL$
 * </pre>
 *
 * @author r39
 */
public class CastTemplateGenerator {


    /** Standard Oracle versioning. */
    public static final String RCS_ID = "$Revision$";


    /** sl4j logger instance. */
    private static final Logger LOGGER = LoggerFactory
        .getLogger(CastTemplateGenerator.class);


    /** Velocity Template file. */
    static final String TEMPLATE_FILE =
            "./src/main/java/junitcast/tool/GenericTest.vm";

    /** */
    private final transient ConsoleInputReader inputReader =
            new ConsoleInputReader();


    /**
     * Command line utility.
     *
     * Usage: GenerateTestCase <resource name> <optional destination directory>
     *
     * @param args Pass the resource path
     * @throws IOException when File IO exception occurs.
     */
    public static void main(final String... args) throws IOException
    {
        //        if (args.length < 1) {
        //            LOGGER
        //                .info("Usage: GenerateTestCase <resource name> <optional destination directory>");
        //        } else {
        //

        LOGGER.info("Enter full property file: ");
        final String propFile = new ConsoleInputReader().readInput();

        final VelocityEngine engine = new VelocityEngine();
        engine.init();

        final VelocityContext context = new VelocityContext();
        Template template = null; //NOPMD: null default, conditionally redefine.
        try {
            template = Velocity.getTemplate(TEMPLATE_FILE);
        } catch (final ResourceNotFoundException e2) {
            LOGGER.error("cannot find template " + TEMPLATE_FILE);
        } catch (final ParseErrorException e) {
            LOGGER.error("Syntax error in template : " + e);
        }

        final String classname = build(context, propFile);
        final StringBuilder outputFile = new StringBuilder();
        if (args.length > 1) {
            final String outpath = args[1];

            outputFile.append(outpath);
            if (!outpath.endsWith(File.separator)) {
                outputFile.append(File.separator);
            }
        }
        outputFile.append(classname).append(".java");
        final File opFile = new File(outputFile.toString());
        if (opFile.exists()) {
            opFile.delete();
        }
        final BufferedWriter writer =
                new BufferedWriter(new FileWriter(opFile));
        if (template != null) {
            template.merge(context, writer);
        }
        writer.flush();
        writer.close();

        LOGGER.info("Output: " + outputFile.toString());
        //        }
    }

    /**
     * Build and return the test class simple name.
     *
     * @param context velocity context instance.
     * @param propFile property file to process.
     */
    static String build(final VelocityContext context, final String propFile)
    {
        final ResourceBundle resBundle = ResourceBundle.getBundle(propFile);
        context.put(Constant.VelocityField.gendate.getParam(), new Date());

        final String pkg = propFile.substring(0, propFile.lastIndexOf('.'));
        context.put(Constant.VelocityField.pkg.getParam(), pkg);

        final String testName =
                propFile.substring(propFile.lastIndexOf('.') + 1);
        String className;
        if (testName.indexOf('_') > -1) {
            className = testName.substring(0, testName.indexOf('_'));
        } else {
            className = testName.replaceAll(testName, "Test");
        }

        context.put(Constant.VelocityField.classname.getParam(), className);
        context.put(Constant.VelocityField.testname.getParam(), testName);

        final List<Map<String, Object>> varList =
                new ArrayList<Map<String, Object>>();
        context.put(Constant.VelocityField.varlist.getParam(), varList);
        final String varRaw =
                resBundle.getString(Constant.ResourceKey.var.name() + "0");

        for (final String nextVarSet : varRaw.split("\\|")) {
            for (final String nextVarName : nextVarSet.trim().split(",")) {
                addVariableName(varList, nextVarName);
            }
        }

        try {
            final String result =
                    resBundle.getString(Constant.ResourceKey.pair.name() + "0");
            final String[] string = StringUtil.trimArray(result.split(":"));
            context
                .put(Constant.VelocityField.resultleft.getParam(), string[0]);
            context.put(
                Constant.VelocityField.resultright.getParam(),
                string[1]);

        } catch (final MissingResourceException ignore) {
            LOGGER.error(ignore.getMessage(), ignore);
        }

        return testName;
    }

    private static void addVariableName(final List<Map<String, Object>> varList,
                                        final String nextString)
    {
        final Map<String, Object> map = new HashMap<String, Object>();

        String prefComma;
        if (varList.isEmpty()) {
            prefComma = "";
        } else {
            prefComma = ", ";
        }

        final String kaso = nextString.trim().replaceAll(" ", "");
        final String var = prefComma + kaso;

        map.put(Constant.VelocityField.var.getParam(), var);
        map.put(Constant.VelocityField.kase.getParam(), kaso);
        varList.add(map);
    }

    ConsoleInputReader getInputReader()
    {
        return this.inputReader;
    }

    /**
     * @see {@link Object#toString()}
     * @return String representation of this instance.
     */
    @Override
    public String toString()
    {
        return super.toString() + " " + RCS_ID;
    }

}
