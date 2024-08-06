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

import com.sparksign.messagebroker.handler.Handler;
import com.sparksign.messagebroker.model.Message;

import java.io.ByteArrayInputStream;

public interface MsgHandler extends Handler {
    void parseMessage(ByteArrayInputStream inputStream);

    void dispatchMessage(Message message);
}
