/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * Copyright © February 2024 Fadimana Kilci - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 *
 * Created by Fadimana Kilci  <fadimekilci07@gmail.com>, August 2024
 */

package com.sparksign.messagebroker.consumer;

import com.sparksign.messagebroker.broker.MessageBroker;
import com.sparksign.messagebroker.message.MsgHandlerService;
import com.sparksign.messagebroker.model.Message;

import java.util.concurrent.TimeUnit;

public class ConsumerService implements Consumer {
    private final MsgHandlerService service     = new MsgHandlerService();
    private final String            name;
    private final MessageBroker broker;
    private final String            queueName;
    private final int               retryLimit;
    private final long              retryDelay;

    public ConsumerService(
            String                  name,
            MessageBroker           broker,
            String                  queueName,
            int                     retryLimit,
            long                    retryDelay
    ) {
        this.name                   = name;
        this.broker                 = broker;
        this.queueName              = queueName;
        this.retryLimit             = retryLimit;
        this.retryDelay             = retryDelay;
    }

    @Override
    public void run() {
        while (true) {
            Message message = broker.consume(queueName);
            if (message != null) {
                try {
                    System.out.println(name + " processing message: " + message.getContent() + "\n");

                    service.handle(message);

                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    e.printStackTrace();
                } catch (Exception e) {
                    message.incrementRetryCount();
                    if (message.getRetryCount() <= retryLimit) {
                        System.out.println(name + " retrying message: " +
                                message.getContent() + " (Attempt: " + message.getRetryCount() + ")");
                        try {
                            TimeUnit.MILLISECONDS.sleep(retryDelay);
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                        }
                        broker.publish(queueName, message);
                    } else {
                        // TODO: Log kaydı işle!
                        System.out.println(name + " failed to process message: " +
                                message.getContent() + " after " + retryLimit + " attempts");
                    }
                    e.printStackTrace();
                }
            }
        }
    }
}
