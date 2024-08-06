/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * Copyright © February 2024 Fadimana Kilci - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 *
 * Created by Fadimana Kilci  <fadimekilci07@gmail.com>, August 2024
 */

package com.sparksign.messagebroker.broker;

import com.sparksign.messagebroker.model.Message;

import java.util.HashMap;
import java.util.Map;

public class MessageBroker {
    private static MessageBroker instance = null;
    private final Map<String, MessageQueue> queues;

    private MessageBroker() {
        this.queues = new HashMap<>();
    }

    public static synchronized MessageBroker getInstance()
    {
        if (instance == null)
            instance = new MessageBroker();

        return instance;
    }

    public void createQueue(String name) {
        if (!queues.containsKey(name)) {
            queues.put(name, new com.sparksign.messagebroker.broker.MessageQueue(name));
            System.out.println("CREATE name = " + name + " - queue = " + queues.size());
        }
    }

    public void publish(String name, Message message) {
        com.sparksign.messagebroker.broker.MessageQueue queue = queues.get(name);
        if (queue != null)
            queue.publish(message);
        else
            System.out.println("Queue not found: " + name + " - queue size: " + queues.size());
    }

    public Message consume(String name) {
        com.sparksign.messagebroker.broker.MessageQueue queue = queues.get(name);
        if (queue != null)
            return queue.consume();
        else {
            System.out.println("Queue not found: " + name);
            return null;
        }
    }

    public boolean isEmpty(String name) {
        com.sparksign.messagebroker.broker.MessageQueue queue = queues.get(name);

        if (queue != null) {
            return queue.isEmpty();
        } else
            return true;
    }
}
