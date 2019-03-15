package org.flowable.cloud.rocketmq.configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.SmartLifecycle;

import org.flowable.cloud.rocketmq.properties.RocketMQProperties;


/**
 * <Description> <br>
 *
 * @author chen.xing01<br>
 * @version 1.0<br>
 */
public class RocketMQConsumer implements SmartLifecycle {
    private static final Logger logger = LoggerFactory.getLogger(RocketMQConsumer.class);
    private DefaultMQPushConsumer consumer;
    private RocketMQProperties properties;
    private Map<String, Consumer> tagToConsumers;
    private volatile boolean running;

    public RocketMQConsumer(RocketMQProperties properties) {
        this.properties = properties;
        this.tagToConsumers = new HashMap<>();
    }

    public void addMessageConsumer(RocketMQMessageConsumer consumer) {
        tagToConsumers.put(consumer.getTagName(), consumer::onMessage);
    }

    @Override
    public void start() {
        if (!this.running) {
            this.running = true;
            this.consumer = new DefaultMQPushConsumer(properties.getGroupName());
            this.consumer.setNamesrvAddr(properties.getNameServiceAddr());
            try {
                this.consumer.subscribe(properties.getGroupName(), (String) null);
                this.consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
                this.consumer.setConsumeThreadMin(5);
                this.consumer.setConsumeThreadMax(20);
                this.consumer.registerMessageListener((List<MessageExt> msgs, ConsumeConcurrentlyContext context) -> {
                    ConsumeConcurrentlyStatus result = ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                    for (MessageExt messageExt : msgs) {
                        String tag = messageExt.getTags();
                        try {
                            tagToConsumers.get(tag).consume(messageExt, context);
                        }
                        catch (Exception t) {
                            logger.error("error", t);
                            result = ConsumeConcurrentlyStatus.RECONSUME_LATER;
                        }
                    }
                    return result;
                });
                this.consumer.start();
            }
            catch (MQClientException t) {
                logger.error("error", t);
                throw new RuntimeException("create rocketmq message producer error!", t);
            }
        }
    }

    @Override
    public void stop() {
        if (this.running && this.consumer != null) {
            this.consumer.shutdown();
        }
        this.running = false;
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public int getPhase() {
        return Integer.MAX_VALUE;
    }

    public interface Consumer {
        void consume(MessageExt messageExt, ConsumeConcurrentlyContext context);
    }
}
