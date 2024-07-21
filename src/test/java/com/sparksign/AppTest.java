package com.sparksign;

import com.sparksign.util.TestUtils;
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
