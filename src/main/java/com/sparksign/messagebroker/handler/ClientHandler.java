/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * Copyright © February 2024 Fadimana Kilci - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 *
 * Created by Fadimana Kilci  <fadimekilci07@gmail.com>, August 2024
 */

package com.sparksign.messagebroker.handler;

import com.sparksign.messagebroker.broker.MessageBroker;
import com.sparksign.messagebroker.connect.Connect;
import com.sparksign.messagebroker.constant.MessageBrokerConstants;
import com.sparksign.messagebroker.model.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ClientHandler implements Handler, Connect {
    private         Socket              socket;
    private         ObjectInputStream   inputStream;
    private         ObjectOutputStream  outputStream;
    private final MessageBroker broker;
    private final   ExecutorService     service;
    private final   ServerSocket        serverSocket;
    private static  ClientHandler       instance        = null;

    private ClientHandler() {
        try {
            this.serverSocket = new ServerSocket(MessageBrokerConstants.clientPort);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        this.broker  = MessageBroker.getInstance();
        this.service = Executors.newCachedThreadPool();

        connect();
    }

    public static synchronized ClientHandler getInstance() {
        if (instance == null)
            instance = new ClientHandler();
        return instance;
    }

    @Override
    public void connect() {
        service.execute(() -> {
            try {
                socket          = serverSocket.accept();
                outputStream    = new ObjectOutputStream(socket.getOutputStream());
                inputStream     = new ObjectInputStream(socket.getInputStream());

                broker.createQueue(MessageBrokerConstants.clientQueueName);

                System.out.println("Connected to client");

                handle(null);
            } catch (UnknownHostException e) {
                System.err.println("Unknown host");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
            } catch (IOException e) {
                System.err.println("Failed to connect to client. Retrying...");
                try {
                    Thread.sleep(5000);
                    if(MessageBrokerConstants.retryLimit.getAndDecrement() != 0) {
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

    @Override
    public void handle(Object object) {
        service.execute(() -> {
            try {
                new Thread(() -> {
                    while (true) {
                        try {
                            Message message = (Message) inputStream.readObject();

                            System.out.println("Received from client: " + message.getContent());

                            broker.publish(MessageBrokerConstants.queueName, message);
                        } catch (IOException | ClassNotFoundException e) {
                            System.err.println("Client disconnected");
                            connect();
                            break;
                        }
                    }
                }).start();

                while (true) {
                    if (!broker.isEmpty(MessageBrokerConstants.clientQueueName)) {
                        Message message = broker.consume(MessageBrokerConstants.clientQueueName);

                        outputStream.writeObject(message);
                        outputStream.flush();

                        System.out.println("Sent to server: " + message.getContent());

                        TimeUnit.SECONDS.sleep(1);
                    }
                    TimeUnit.SECONDS.sleep(3);
                }
            } catch (IOException | InterruptedException e) {
                System.err.println("Client disconnected");
                e.printStackTrace();
            }
        });
    }
}
