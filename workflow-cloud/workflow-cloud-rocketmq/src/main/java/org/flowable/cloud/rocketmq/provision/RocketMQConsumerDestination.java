package org.flowable.cloud.rocketmq.provision;

import org.springframework.cloud.stream.provisioning.ConsumerDestination;

/**
 * <Description> <br>
 *
 * @author chen.xing01<br>
 * @version 1.0<br>
 */
public class RocketMQConsumerDestination implements ConsumerDestination {
    private String name;

    public RocketMQConsumerDestination(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}
