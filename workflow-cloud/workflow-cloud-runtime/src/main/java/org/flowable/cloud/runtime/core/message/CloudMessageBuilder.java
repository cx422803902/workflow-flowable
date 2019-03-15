package org.flowable.cloud.runtime.core.message;

import java.util.ArrayList;
import java.util.List;

import org.springframework.messaging.support.MessageBuilder;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.util.Assert;

/**
 * @author chen.xing<br>
 * @version 1.0<br>
 * @Desc good study day day up
 * @data 2019<br>
 */
public class CloudMessageBuilder {
    private static final String MESSAGE_PAYLOAD_TYPE = "messagePayloadType";

    private final List<CloudMessageHandler> messageHandlers = new ArrayList<>();

    public CloudMessageBuilder() {
    }

    public <P> MessageBuilder<P> withPayload(P payload) {
        Assert.notNull(payload, "payload must not be null");

        // So we can access headers later
        MessageHeaderAccessor accessor = new MessageHeaderAccessor();
        accessor.setLeaveMutable(true);

        MessageBuilder messageBuilder = MessageBuilder.withPayload(payload)
                .setHeaders(accessor);
        // Let's resolve payload class name
        messageBuilder.setHeader(MESSAGE_PAYLOAD_TYPE, payload.getClass().getName());

        for (CloudMessageHandler messageHandler : messageHandlers) {
            messageHandler.resolve(messageBuilder, accessor);
        }

        return messageBuilder;
    }

    public CloudMessageBuilder handler(CloudMessageHandler messageHandler) {
        Assert.notNull(messageHandler, "filter must not be null");
        messageHandlers.add(messageHandler);
        return this;
    }
}
