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

package com.sparksign.client;

import com.sparksign.broker.Consumer;
import com.sparksign.broker.MessageBroker;
import com.sparksign.model.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Client {
    private final MessageBroker broker;
    private final String                host;
    private final int                   port;
    private       AtomicInteger         retryLimit;
    private final ExecutorService       service;
    private       Socket                socket;
    private final String                queueName;
    private ObjectInputStream           inputStream;
    private ObjectOutputStream          outputStream;

    public Client(String host, int port) {
        this.broker             = MessageBroker.getInstance();
        this.host               = host;
        this.port               = port;
        this.retryLimit         = new AtomicInteger(3);
        this.service            = Executors.newCachedThreadPool();
        this.queueName          = "default";

        // TODO: REMOVED
        broker.createQueue(queueName);
    }

    public void start() {
        service.execute(new Consumer(
                "Consumer Client",
                broker,
                queueName,
                3,
                1000));

        service.execute(this::connect);
    }

    private void handleServer() {
        service.execute(() -> {
            try {
                new Thread(() -> {
                    while (true) {
                        try {
                            Message message = (Message) inputStream.readObject();
                            System.out.println("Received from server: " + message.getContent());
                            broker.publish(queueName, message);
                        } catch (IOException | ClassNotFoundException e) {
                            System.err.println("Server disconnected.");
                            retryConnection();
                            break;
                        }
                    }
                }).start();

                while (true) {
                    if (!broker.isEmpty(queueName)) {
                        Message message = broker.consume(queueName);
                        outputStream.writeObject(message);
                        outputStream.flush();
                        System.out.println("Sent to server: " + message.getContent());
                        TimeUnit.SECONDS.sleep(1);
                    }
                    TimeUnit.SECONDS.sleep(3);
                }
            } catch (IOException | InterruptedException e) {
                System.err.println("Exception = " + e);
                throw new RuntimeException(e);
            }
        });
    }

    public void connect() {
        service.execute(() -> {
            try {
                socket = new Socket(host, port);
                outputStream = new ObjectOutputStream(socket.getOutputStream());
                inputStream = new ObjectInputStream(socket.getInputStream());
                System.out.println("Connected to server");
                clearRetry();
                handleServer();
            } catch (UnknownHostException e) {
                System.err.println("Unknown host: " + host);
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
            } catch (IOException e) {
                retryConnection();
            }
        });
    }

    public void disconnect() {
        try {
            retryLimit.set(0);
            socket.close();
            System.out.println("DISCONNECTED");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void clearRetry() {
        retryLimit.set(3);
    }

    private void retryConnection() {
        try {
            System.err.println("Failed to connect to server. Remaining retry " + retryLimit.get() + "...");
            Thread.sleep(2000);
            if(retryLimit.getAndDecrement() != 0) {
                connect();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void sendMessage(String content) {
        if(socket.isConnected()) {
            try {
                // TODO: Bak!
                Message message = new Message(content);
//            broker.publish(queueName, message);
                outputStream.writeObject(message);
                outputStream.flush();
//                System.out.println("Publish Client Message: " + message.getId() + " - " + message.getContent() + " - " + message.getTimestamp());
            } catch (Exception e) {
                System.err.println("Failed to send message!");
            }
        } else {
            // Local DB
        }
    }

//    public void receiveMessages() {
//        new Thread(() -> {
//            while (true) {
//                try {
//                    Message message = (Message) input.readObject();
//                    System.out.println("Received from server: " + message.getContent());
//                } catch (IOException | ClassNotFoundException e) {
//                    System.err.println("Disconnected from server: " + e.getMessage());
////                    connect();
//                    retryConnection();
//                    break;
//                }
//            }
//        }).start();
//    }

    public static void main(String[] args) throws InterruptedException, IOException {
        Client client = new Client("localhost", 1234);
        client.start();

        TimeUnit.SECONDS.sleep(2);
        for (int i = 1; i <= 10; i++) {
            client.sendMessage("Message from client " + i);
            TimeUnit.SECONDS.sleep(2);
        }

        TimeUnit.SECONDS.sleep(15);
        client.disconnect();
    }
}
