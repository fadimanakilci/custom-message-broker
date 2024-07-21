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

package com.sparksign.server;

import com.sparksign.broker.Consumer;
import com.sparksign.broker.MessageBroker;
import com.sparksign.model.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Server {
    private final MessageBroker broker;
    private final int                   port;
    private       AtomicInteger         retryLimit;
    private final ExecutorService       service;
    private       ServerSocket          serverSocket;
    private       Socket                clientSocket;
    private final String                queueName;
    private ObjectInputStream           inputStream;
    private ObjectOutputStream          outputStream;

    public Server(int port) {
        this.broker             = MessageBroker.getInstance();
        this.port               = port;
        this.retryLimit         = new AtomicInteger(3);
        this.service            = Executors.newCachedThreadPool();
        this.queueName          = "default";

        broker.createQueue(queueName);
    }

    public void start() {
        service.execute(new Consumer(
                        "Consumer Server",
                        broker,
                        queueName,
                        3,
                        1000));
        service.execute(new Consumer("Consumer 1", broker, queueName, 3, 1000));
        service.execute(new Consumer("Consumer 2", broker, queueName, 3, 1000));

        service.execute(() -> {
            try {
                serverSocket = new ServerSocket(port);
                connect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        service.execute(() -> {
            try {
                Thread.sleep(1000);
                broker.publish(queueName, new Message("Hello, World!"));
                Thread.sleep(1000);
                broker.publish(queueName, new Message("Another message"));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                e.printStackTrace();
            }
        });
    }

    private void handleClient() {
        service.execute(() -> {
            try {
                new Thread(() -> {
                    while (true) {
                        try {
                            Message message = (Message) inputStream.readObject();
                            System.out.println("Received from client: " + message.getContent());
                            broker.publish(queueName, message);
//                            TimeUnit.SECONDS.sleep(3);
//                            Message msg = new Message("hubalala");
//                            output.writeObject(msg);
//                            System.out.println("Sent to client: " + message.getContent());
                        } catch (IOException | ClassNotFoundException e) {
                            System.err.println("Client disconnected.");
                            connect();
                            break;
                        }
                    }
                }).start();

                while (true) {
                    if (!broker.isEmpty(queueName)) {
                        Message message = broker.consume(queueName);
                        outputStream.writeObject(message);
                        outputStream.flush();
                        System.out.println("Sent to client: " + message.getContent());
                        TimeUnit.SECONDS.sleep(1);
                    }
                    TimeUnit.SECONDS.sleep(3);
                }
            } catch (IOException | InterruptedException e) {
                System.err.println("Client disconnecteddd.");
                e.printStackTrace();
            }
        });
    }

    public void connect() {
        service.execute(() -> {
            try {
                clientSocket = serverSocket.accept();
                outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
                inputStream = new ObjectInputStream(clientSocket.getInputStream());
                System.out.println("Connected to client");
                handleClient();
            } catch (UnknownHostException e) {
                System.err.println("Unknown host");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
            } catch (IOException e) {
//                throw new RuntimeException(e);
                System.err.println("Failed to connect to client. Retrying...");
                try {
                    Thread.sleep(5000);
                    if(retryLimit.getAndDecrement() != 0) {
                        connect();
                    }
                } catch (InterruptedException interruptedException) {
                    throw new RuntimeException(interruptedException);
                }
            } catch (Exception e) {
                System.err.println("Exception: " + e);
            }
        });
    }

    public void sendMessage(String content) {
        if(clientSocket != null && clientSocket.isConnected()) {
            try {
                // TODO: Bak!
                Message message = new Message(content);
//            broker.publish(queueName, message);
                outputStream.writeObject(message);
                outputStream.flush();
//                System.out.println("Publish Server Message: " + message.getId() + " - " + message.getContent() + " - " + message.getTimestamp());
            } catch (Exception e) {
                System.err.println("Failed to send message!");
            }
        } else {
            // Local DB
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Server server = new Server(1234);
        server.start();

        TimeUnit.SECONDS.sleep(10);
        for (int i = 1; i <= 10; i++) {
            server.sendMessage("Message from server " + i);
            TimeUnit.SECONDS.sleep(2);
        }
    }
}
