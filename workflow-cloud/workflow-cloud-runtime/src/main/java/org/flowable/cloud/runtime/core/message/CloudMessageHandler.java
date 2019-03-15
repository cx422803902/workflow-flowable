package org.flowable.cloud.runtime.core.message;

import org.springframework.messaging.support.MessageBuilder;
import org.springframework.messaging.support.MessageHeaderAccessor;

/**
 * @author chen.xing<br>
 * @version 1.0<br>
 * @Desc good study day day up
 * @data 2019<br>
 */
public interface CloudMessageHandler {
    <P> void resolve(MessageBuilder<P> request, MessageHeaderAccessor accessor);
}
