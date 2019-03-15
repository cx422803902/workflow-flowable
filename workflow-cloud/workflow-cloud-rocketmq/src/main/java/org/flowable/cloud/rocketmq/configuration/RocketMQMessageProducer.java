package org.flowable.cloud.rocketmq.configuration;

import org.apache.rocketmq.client.producer.SendResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.handler.AbstractMessageHandler;
import org.springframework.messaging.Message;

import com.alibaba.fastjson.JSON;
import org.flowable.cloud.rocketmq.properties.RocketMQProducerProperties;
import org.flowable.cloud.rocketmq.properties.RocketMQProperties;

/**
 * <Description> <br>
 *
 * @author chen.xing01<br>
 * @version 1.0<br>
 */
public class RocketMQMessageProducer extends AbstractMessageHandler {

    private static final Logger logger = LoggerFactory.getLogger(RocketMQMessageProducer.class);

    private RocketMQProperties properties;
    private RocketMQProducerProperties producerProperties;
    private RocketMQProducer producer;
    private String tagName;

    public RocketMQProperties getProperties() {
        return properties;
    }

    public void setProperties(RocketMQProperties properties) {
        this.properties = properties;
    }

    public RocketMQProducerProperties getProducerProperties() {
        return producerProperties;
    }

    public void setProducerProperties(RocketMQProducerProperties producerProperties) {
        this.producerProperties = producerProperties;
    }

    public RocketMQProducer getProducer() {
        return producer;
    }

    public void setProducer(RocketMQProducer producer) {
        this.producer = producer;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    @Override
    protected void handleMessageInternal(Message<?> message) throws Exception {
        if (producer != null) {
            SendResult sendResult = producer.handleMessage(tagName, convert(message));
            logger.debug("result status is " + JSON.toJSONString(sendResult.getSendStatus()));
        }
    }

    private org.apache.rocketmq.common.message.Message convert(Message<?> message) {
        byte[] payload = (byte[]) message.getPayload();
        org.apache.rocketmq.common.message.Message msg = new org.apache.rocketmq.common.message.Message();
        msg.setBody(payload);
        return msg;
    }
}
