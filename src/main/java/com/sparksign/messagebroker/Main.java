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

import com.sparksign.messagebroker.broker.MessageBroker;
import com.sparksign.messagebroker.connect.Connect;
import com.sparksign.messagebroker.constant.MessageBrokerConstants;
import com.sparksign.messagebroker.handler.ClientHandler;
import com.sparksign.messagebroker.handler.ConsumerHandler;
import com.sparksign.messagebroker.handler.ServerHandler;

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
