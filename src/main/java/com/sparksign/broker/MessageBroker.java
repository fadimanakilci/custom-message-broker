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
            queues.put(name, new MessageQueue(name));
//            System.out.println("CREATE name = " + name + " - queue = " + queues.size());
        }
    }

    public void publish(String name, Message message) {
//        System.out.println("name = " + name + ", message = " + message);
        MessageQueue queue = queues.get(name);
        if (queue != null)
            queue.publish(message);
        else
            System.out.println("Queue not found: " + name + " - queue size: " + queues.size());
    }

    public Message consume(String name) {
        MessageQueue queue = queues.get(name);
        if (queue != null)
            return queue.consume();
        else {
            System.out.println("Queue not found: " + name);
            return null;
        }
    }

    public boolean isEmpty(String name) {
        MessageQueue queue = queues.get(name);
//        System.out.println("name = " + name + " - queue = " + queue.isEmpty());

        if (queue != null) {
            return queue.isEmpty();
        } else
            return true;
    }
}
