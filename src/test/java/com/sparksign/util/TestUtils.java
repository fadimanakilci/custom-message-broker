/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * Copyright © February 2024 Fadimana Kilci - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 *
 * Created by Fadimana Kilci  <fadimekilci07@gmail.com>, February 2024
 */

package com.sparksign.util;

public class TestUtils {
    long beforeUsedMem;
    long startTime;
    long endTime;
    long actualMemUsed;

    public void startScheduler() {
        beforeUsedMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        startTime = System.currentTimeMillis();
    }

    public void stopScheduler() {
        endTime       = System.currentTimeMillis();
        long afterUsedMem  = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        actualMemUsed = afterUsedMem - beforeUsedMem;
        out();
    }

    private void out() {
        System.out.println(
                "\n\n############################# PERFORMANCE STATS #############################" +
                        "\nTime: " + (endTime - startTime) + "ms" +
                        "\nMemoryUsed: " + (actualMemUsed) + " bytes" +
                        "\nMemoryUsed: " + (actualMemUsed / (1024)) + " kb" +
                        "\nMemoryUsed: " + (actualMemUsed / (1024 * 1024)) + " mb" +
                        "\n############################# PERFORMANCE STATS #############################\n\n"
        );
    }
}
