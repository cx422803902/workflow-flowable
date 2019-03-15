package org.flowable.cloud.rocketmq.properties;

import org.springframework.cloud.stream.binder.BinderSpecificPropertiesProvider;

/**
 * <Description> <br>
 *
 * @author chen.xing01<br>
 * @version 1.0<br>
 * 暂时无用
 */
public class RocketMQBinderSpecificPropertiesProvider implements BinderSpecificPropertiesProvider {
    private RocketMQConsumerProperties consumer = new RocketMQConsumerProperties();
    private RocketMQProducerProperties producer = new RocketMQProducerProperties();

    @Override
    public RocketMQConsumerProperties getConsumer() {
        return consumer;
    }

    public void setConsumer(RocketMQConsumerProperties consumer) {
        this.consumer = consumer;
    }

    @Override
    public RocketMQProducerProperties getProducer() {
        return producer;
    }

    public void setProducer(RocketMQProducerProperties producer) {
        this.producer = producer;
    }
}
