package org.flowable.cloud.rocketmq.properties;

import org.springframework.cloud.stream.binder.AbstractExtendedBindingProperties;
import org.springframework.cloud.stream.binder.BinderSpecificPropertiesProvider;

/**
 * <Description> <br>
 *
 * @author chen.xing01<br>
 * @version 1.0<br>
 */
public class RocketMQExtendedBindingProperties extends AbstractExtendedBindingProperties<RocketMQConsumerProperties, RocketMQProducerProperties, RocketMQBinderSpecificPropertiesProvider> {
    private static final String DEFAULTS_PREFIX = "spring.cloud.stream.rocket.default";

    @Override
    public String getDefaultsPrefix() {
        return DEFAULTS_PREFIX;
    }

    @Override
    public Class<? extends BinderSpecificPropertiesProvider> getExtendedPropertiesEntryClass() {
        return RocketMQBinderSpecificPropertiesProvider.class;
    }
}
