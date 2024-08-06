/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * Copyright © February 2024 Fadimana Kilci - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 *
 * Created by Fadimana Kilci  <fadimekilci07@gmail.com>, August 2024
 */

package com.sparksign.handler;
import com.sparksign.broker.MessageBroker;
import com.sparksign.constant.MessageBrokerConstants;
import com.sparksign.consumer.ConsumerService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConsumerHandler implements Handler {
    private final   List<String>      consumers;
    private final MessageBroker broker;
    private final   ExecutorService   service;
    private static  ConsumerHandler   instance = null;

    private ConsumerHandler() {
        this.consumers      = new ArrayList<>();
        this.broker         = MessageBroker.getInstance();
        this.service        = Executors.newSingleThreadExecutor();

    }

    public static synchronized ConsumerHandler getInstance() {
        if (instance == null)
            instance = new ConsumerHandler();
        return instance;
    }

    @Override
    public void handle(Object object) {
        if(!consumers.contains((String) object)){
            service.execute(new ConsumerService(
                    (String) object,
                    broker,
                    MessageBrokerConstants.queueName,
                    3,
                    1000));
            consumers.add((String) object);
            System.out.println("Create: " + object);
        }
    }
}
