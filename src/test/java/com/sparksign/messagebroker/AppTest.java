/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * Copyright © February 2024 Fadimana Kilci - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 *
 * Created by Fadimana Kilci  <fadimekilci07@gmail.com>, August 2024
 */

package com.sparksign.messagebroker;

import com.sparksign.messagebroker.util.TestUtils;
import org.junit.*;

public class AppTest {
    private static final TestUtils testUtils = new TestUtils();
    private static void out(Object object) {
        System.out.println(object);
    }

    @Before
    public void setUp() {
        out("____________________________ BEFORE ____________________________");
        testUtils.startScheduler();
    }

    @After
    public void tearDown() {
        testUtils.stopScheduler();
        out("____________________________ AFTER ____________________________");
    }

    @Ignore
    @Test
    public void testApp()
    {
        Assert.assertTrue(true);
    }
}
