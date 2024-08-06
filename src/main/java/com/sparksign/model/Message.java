/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * Copyright © February 2024 Fadimana Kilci - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 *
 * Created by Fadimana Kilci  <fadimekilci07@gmail.com>, August 2024
 */

package com.sparksign.model;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

public class Message implements Serializable {
    private final AtomicInteger     id;
    private final String            content;
    private final String            status;
    private       int               retryCount;
    private final long              timestamp;

    public Message(String content) {
        this.id                     = new AtomicInteger(0);
        this.content                = content;
        this.status                 = "pending";
        this.retryCount             = 0;
        this.timestamp              = System.currentTimeMillis();
    }

    public int getId() {
        return id.get();
    }

    public String getContent() {
        return content;
    }

    public String getStatus() {
        return status;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void incrementId() {
        id.incrementAndGet();
    }

    public void incrementRetryCount() {
        this.retryCount += 1;
    }

    @Override
    public String toString() {
        return getId() + ", " +
                getContent() + ", " +
                getStatus() + ", " +
                getRetryCount() + ", " +
                getTimestamp();
    }
}
