/**
 * Copyright 2014 Asian Development Bank. All rights reserved.
 * ADB PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Project Name:  ISTSII ADB Manage Procurement Review System.
 * Module :  Monitoring
 * Use Case:
 * Purpose:
 * Design Document Reference:  <full path where the design document is and page no>
 * File Path: $HeadURL$
 *
 * Oracle ERP Release: 12.1.3
 * Oracle ERP Module: Custom.
 *
 * Created by: Royce Remulla (R39)
 */
package junitcast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;


/**
 * 
 * @author Royce Remulla
 * @version $Date$
 */
public class ListMergerTest {


    /** */
    private final transient ListMerger<String> sut = new ListMerger<String>();


    /**
     * Test basic scenario.
     */
    @Test
    public void simpleTest()
    {
        final List<String> list = new ArrayList<String>(
            Arrays.asList(new String[] {
                    "false",
                    "true" }));

        final List<List<String>> all = new ArrayList<List<String>>();
        all.add(list);
        all.add(list);

        final List<List<String>> result = sut.merge(all);

        Assert.assertEquals(
            "[[false, false], [false, true], [true, false], [true, true]]",
            result.toString());

    }
}
