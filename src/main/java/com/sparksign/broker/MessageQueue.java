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

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.Queue;

public class MessageQueue {
    private Queue<Message> queue  = new LinkedList<>();
    private final String name;
    private final Object lock = new Object();

    public MessageQueue(String name) {
        this.name   = name;
        loadMessages();
    }

    public void loadMessages() {
        try (ObjectInputStream objectInputStream =
                     new ObjectInputStream(new FileInputStream(name + ".queue"))) {
            queue = (Queue<Message>) objectInputStream.readObject();
        } catch (FileNotFoundException e) {
            System.out.println("No previous messages found, starting with an empty queue.");
        }  catch (IOException | ClassNotFoundException e) {
//            queue = new LinkedList<>();
            e.printStackTrace();
        }
    }

    public void saveMessages() {
        try (ObjectOutputStream objectOutputStream =
                     new ObjectOutputStream(Files.newOutputStream(Paths.get(name + ".queue")))) {
            objectOutputStream.writeObject(queue);
//            objectOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void publish(Message message) {
//        System.out.println("message = " + message.getContent());
        synchronized (lock) {
            queue.offer(message);
            saveMessages();
            lock.notifyAll();
        }
    }

    public Message consume() {
        synchronized (lock) {
            while (queue.isEmpty()) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    e.printStackTrace();
                    return null;
                }
            }
            Message message = queue.poll();
            saveMessages();
            return message;
        }
    }

    public boolean isEmpty() {
        synchronized (lock) {
//            System.out.println("QUEUE name = " + name + " - queue = " + queue.isEmpty());
            return queue.isEmpty();
        }
    }


}
