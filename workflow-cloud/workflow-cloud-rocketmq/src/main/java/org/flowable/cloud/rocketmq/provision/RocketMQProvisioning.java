package org.flowable.cloud.rocketmq.provision;

import org.flowable.cloud.rocketmq.properties.RocketMQConsumerProperties;
import org.springframework.cloud.stream.binder.ExtendedConsumerProperties;
import org.springframework.cloud.stream.binder.ExtendedProducerProperties;
import org.springframework.cloud.stream.provisioning.ConsumerDestination;
import org.springframework.cloud.stream.provisioning.ProducerDestination;
import org.springframework.cloud.stream.provisioning.ProvisioningException;
import org.springframework.cloud.stream.provisioning.ProvisioningProvider;

import org.flowable.cloud.rocketmq.properties.RocketMQProducerProperties;


/**
 * <Description> <br>
 *
 * @author chen.xing01<br>
 * @version 1.0<br>
 */
public class RocketMQProvisioning implements ProvisioningProvider<ExtendedConsumerProperties<RocketMQConsumerProperties>, ExtendedProducerProperties<RocketMQProducerProperties>> {
    @Override
    public ProducerDestination provisionProducerDestination(String name, ExtendedProducerProperties<RocketMQProducerProperties> properties) throws ProvisioningException {
        return new RocketMQProducerDestination(name);
    }

    @Override
    public ConsumerDestination provisionConsumerDestination(String name, String group, ExtendedConsumerProperties<RocketMQConsumerProperties> properties) throws ProvisioningException {
        return new RocketMQConsumerDestination(name);
    }
}
