package org.flowable.cloud.rocketmq.configuration;

import org.flowable.cloud.rocketmq.properties.RocketMQConsumerProperties;
import org.springframework.cloud.stream.binder.AbstractMessageChannelBinder;
import org.springframework.cloud.stream.binder.BinderSpecificPropertiesProvider;
import org.springframework.cloud.stream.binder.ExtendedConsumerProperties;
import org.springframework.cloud.stream.binder.ExtendedProducerProperties;
import org.springframework.cloud.stream.binder.ExtendedPropertiesBinder;
import org.springframework.cloud.stream.provisioning.ConsumerDestination;
import org.springframework.cloud.stream.provisioning.ProducerDestination;
import org.springframework.integration.core.MessageProducer;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

import org.flowable.cloud.rocketmq.properties.RocketMQExtendedBindingProperties;
import org.flowable.cloud.rocketmq.properties.RocketMQProducerProperties;
import org.flowable.cloud.rocketmq.properties.RocketMQProperties;
import org.flowable.cloud.rocketmq.provision.RocketMQProvisioning;


/**
 * <Description> <br>
 *
 * @author chen.xing01<br>
 * @version 1.0<br>
 */
public class RocketMQMessageChannelBinder extends AbstractMessageChannelBinder<ExtendedConsumerProperties<RocketMQConsumerProperties>, ExtendedProducerProperties<RocketMQProducerProperties>, RocketMQProvisioning> implements ExtendedPropertiesBinder<MessageChannel, RocketMQConsumerProperties, RocketMQProducerProperties> {

    private RocketMQProperties properties;

    private RocketMQExtendedBindingProperties rocketMQExtendedBindingProperties;

    private RocketMQProducer producer;
    private RocketMQConsumer consumer;

    public RocketMQMessageChannelBinder(RocketMQProducer producer, RocketMQConsumer consumer, RocketMQProperties properties, RocketMQExtendedBindingProperties rocketMQExtendedBindingProperties, RocketMQProvisioning provisioningProvider) {
        super(new String[0], provisioningProvider);
        this.properties = properties;
        this.rocketMQExtendedBindingProperties = rocketMQExtendedBindingProperties;
        this.producer = producer;
        this.consumer = consumer;
    }

    @Override
    protected MessageHandler createProducerMessageHandler(ProducerDestination destination, ExtendedProducerProperties<RocketMQProducerProperties> producerProperties, MessageChannel errorChannel) throws Exception {
        RocketMQMessageProducer messageProducer = new RocketMQMessageProducer();
        messageProducer.setProperties(properties);
        messageProducer.setProducerProperties(producerProperties.getExtension());
        messageProducer.setTagName(destination.getName());
        messageProducer.setProducer(producer);
        return messageProducer;
    }

    @Override
    protected MessageProducer createConsumerEndpoint(ConsumerDestination destination, String group, ExtendedConsumerProperties<RocketMQConsumerProperties> consumerProperties) throws Exception {
        RocketMQMessageConsumer messageConsumer = new RocketMQMessageConsumer();
        messageConsumer.setProperties(properties);
        messageConsumer.setConsumerProperties(consumerProperties.getExtension());
        messageConsumer.setTagName(destination.getName());
        consumer.addMessageConsumer(messageConsumer);
        return messageConsumer;
    }

    @Override
    public RocketMQConsumerProperties getExtendedConsumerProperties(String channelName) {
        return rocketMQExtendedBindingProperties.getExtendedConsumerProperties(channelName);
    }

    @Override
    public RocketMQProducerProperties getExtendedProducerProperties(String channelName) {
        return rocketMQExtendedBindingProperties.getExtendedProducerProperties(channelName);
    }

    @Override
    public String getDefaultsPrefix() {
        return rocketMQExtendedBindingProperties.getDefaultsPrefix();
    }

    @Override
    public Class<? extends BinderSpecificPropertiesProvider> getExtendedPropertiesEntryClass() {
        return rocketMQExtendedBindingProperties.getExtendedPropertiesEntryClass();
    }
}