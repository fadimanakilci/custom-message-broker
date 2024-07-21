/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * Copyright © (C) 2023 Yerlem - All Rights Reserved
 *  Unauthorized copying of this file, via any medium is strictly prohibited
 *  Proprietary and confidential.
 *
 *  Copyright © October 2023 Yerlem  @ https://yerlem.com
 *  Written by Fadimana Kilci  <fadimekilci07@gmail.com>, July 2024
 */

package com.sparksign.broker;

import com.sparksign.model.Message;

import java.util.concurrent.TimeUnit;

public class Consumer implements Runnable {
    private final String            name;
    private final MessageBroker     broker;
    private final String            queueName;
    private final int               retryLimit;
    private final long              retryDelay;

    public Consumer(
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
                    // TODO: Gerekli işlem yönlendirmelerin burada olacak!
                    System.out.println(name + " processing message: " + message.getContent());

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
