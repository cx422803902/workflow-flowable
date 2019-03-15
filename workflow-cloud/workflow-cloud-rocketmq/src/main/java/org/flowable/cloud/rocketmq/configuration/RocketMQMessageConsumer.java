package org.flowable.cloud.rocketmq.configuration;

import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.common.message.MessageExt;
import org.flowable.cloud.rocketmq.properties.RocketMQConsumerProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.binder.EmbeddedHeaderUtils;
import org.springframework.cloud.stream.binder.MessageValues;
import org.springframework.integration.endpoint.MessageProducerSupport;
import org.springframework.messaging.Message;

import org.flowable.cloud.rocketmq.properties.RocketMQProperties;


/**
 * <Description> <br>
 *
 * @author chen.xing01<br>
 * @version 1.0<br>
 */
public class RocketMQMessageConsumer extends MessageProducerSupport {
    private static final Logger logger = LoggerFactory.getLogger(RocketMQMessageConsumer.class);
    private RocketMQProperties properties;
    private String tagName;
    private RocketMQConsumerProperties consumerProperties;

    public void setProperties(RocketMQProperties properties) {
        this.properties = properties;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public RocketMQProperties getProperties() {
        return properties;
    }

    public RocketMQConsumerProperties getConsumerProperties() {
        return consumerProperties;
    }

    public void setConsumerProperties(RocketMQConsumerProperties consumerProperties) {
        this.consumerProperties = consumerProperties;
    }

    public void onMessage(MessageExt messageExt, ConsumeConcurrentlyContext context) {
        try {
            //TODO deal with duplication of consume
            sendMessage(convert(messageExt));
        }
        catch (Exception t) {
            logger.error("error", t);
        }
    }

    private Message<?> convert(MessageExt message) throws Exception {
        MessageValues value = EmbeddedHeaderUtils.extractHeaders(message.getBody());
        return getMessageBuilderFactory()
                .withPayload(value.getPayload())
                .copyHeaders(value.getHeaders())
                .build();

    }

}
