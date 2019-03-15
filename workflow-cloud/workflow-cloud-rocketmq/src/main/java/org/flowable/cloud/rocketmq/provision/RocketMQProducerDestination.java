package org.flowable.cloud.rocketmq.provision;

import org.springframework.cloud.stream.provisioning.ProducerDestination;

/**
 * <Description> <br>
 *
 * @author chen.xing01<br>
 * @version 1.0<br>
 */
public class RocketMQProducerDestination implements ProducerDestination {
    private String name;

    public RocketMQProducerDestination(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getNameForPartition(int partition) {
        return name;
    }
}
