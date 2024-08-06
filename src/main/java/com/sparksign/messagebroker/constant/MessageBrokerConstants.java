/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * Copyright © (C) 2023 Yerlem - All Rights Reserved
 *  Unauthorized copying of this file, via any medium is strictly prohibited
 *  Proprietary and confidential.
 *
 *  Copyright © October 2023 Yerlem  @ https://yerlem.com
 *  Written by Fadimana Kilci  <fadimekilci07@gmail.com>, August 2024
 */

package com.sparksign.messagebroker.constant;

import java.util.concurrent.atomic.AtomicInteger;

public final class MessageBrokerConstants {
    public static final int           clientPort                = 1234;
    public static final int           serverPort                = 5678;

    public static final String        queueName                 = "default";
    public static final String        clientQueueName           = "client";
    public static final String        serverQueueName           = "server";
    public static final String        consumerName              = "Consumer";

    public static final String        clientConsumerName        = consumerName + "Client";
    public static final String        serverConsumerName        = consumerName + "Server";

    public static final AtomicInteger retryLimit                = new AtomicInteger(3);
}
