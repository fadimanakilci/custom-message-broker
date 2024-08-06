/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * Copyright © February 2024 Fadimana Kilci - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 *
 * Created by Fadimana Kilci  <fadimekilci07@gmail.com>, August 2024
 */

package com.sparksign.messagebroker.message;

import com.sparksign.messagebroker.model.Message;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

public final class MsgHandlerService implements MsgHandler {
    public MsgHandlerService() {}

    @Override
    public void handle(Object message) {
        String _message = ((Message) message).getContent();
        byte[] bytes                        = _message.getBytes(StandardCharsets.UTF_8);
        ByteArrayInputStream inputStream    = new ByteArrayInputStream(bytes);

        parseMessage(inputStream);
    }

    @Override
    public void parseMessage(ByteArrayInputStream inputStream) {
        // TODO: Parse
    }

    // Mesajları dağıtmak ve gerekli işlemleri yapmak için kullanılır
    // message klasöründe ki message lere taşı
    @Override
    public void dispatchMessage(Message message) {
        // TODO: Dispatch
    }
}
