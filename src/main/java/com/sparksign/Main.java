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

package com.sparksign;

import com.sparksign.broker.MessageBroker;
import com.sparksign.connect.Connect;
import com.sparksign.constant.MessageBrokerConstants;
import com.sparksign.handler.ClientHandler;
import com.sparksign.handler.ConsumerHandler;
import com.sparksign.handler.ServerHandler;

public class Main {
    public static void main(String[] args) {
        ConsumerHandler consumerHandler = ConsumerHandler.getInstance();
        MessageBroker.getInstance().createQueue(MessageBrokerConstants.queueName);

        Connect serverConnect = ServerHandler.getInstance();
        consumerHandler.handle(MessageBrokerConstants.serverConsumerName);

        Connect clientConnect = ClientHandler.getInstance();
        consumerHandler.handle(MessageBrokerConstants.clientConsumerName);
    }
}
