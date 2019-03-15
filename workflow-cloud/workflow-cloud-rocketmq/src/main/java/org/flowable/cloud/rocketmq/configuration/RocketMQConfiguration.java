package org.flowable.cloud.rocketmq.configuration;


import org.flowable.cloud.rocketmq.provision.RocketMQProvisioning;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import org.flowable.cloud.rocketmq.properties.RocketMQExtendedBindingProperties;
import org.flowable.cloud.rocketmq.properties.RocketMQProperties;


/**
 * <Description> <br>
 *
 * @author chen.xing01<br>
 * @version 1.0<br>
 */

@Configuration
@ConditionalOnMissingBean(Binder.class)
@PropertySource("classpath:/application-rocketmq.properties")
public class RocketMQConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "spring.cloud.stream.rocketmq")
    public RocketMQProperties rocketMQProperties() {
        return new RocketMQProperties();
    }

    @Bean
    public RocketMQExtendedBindingProperties rocketMQExtendedBindingProperties() {
        return new RocketMQExtendedBindingProperties();
    }

    @Bean
    public RocketMQProvisioning rocketMQProvisioning() {
        return new RocketMQProvisioning();
    }

    @Bean
    public RocketMQMessageChannelBinder rocketMQMessageChannelBinder(RocketMQProducer producer, RocketMQConsumer consumer, RocketMQProperties properties, RocketMQExtendedBindingProperties rocketMQExtendedBindingProperties, RocketMQProvisioning provisioningProvider) {
        return new RocketMQMessageChannelBinder(producer, consumer, properties, rocketMQExtendedBindingProperties, provisioningProvider);
    }

    @Bean
    public RocketMQProducer rocketMQProducer(RocketMQProperties properties) {
        return new RocketMQProducer(properties);
    }

    @Bean
    public RocketMQConsumer rocketMQConsumer(RocketMQProperties properties) {
        return new RocketMQConsumer(properties);
    }
}
