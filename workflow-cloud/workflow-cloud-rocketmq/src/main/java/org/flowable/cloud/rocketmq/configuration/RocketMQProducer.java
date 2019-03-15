package org.flowable.cloud.rocketmq.configuration;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.SmartLifecycle;

import com.alibaba.fastjson.JSON;
import org.flowable.cloud.rocketmq.properties.RocketMQProperties;

/**
 * <Description> <br>
 *
 * @author chen.xing01<br>
 * @version 1.0<br>
 */
public class RocketMQProducer implements SmartLifecycle {
    private static final Logger logger = LoggerFactory.getLogger(RocketMQMessageProducer.class);
    private RocketMQProperties properties;
    private volatile boolean running;
    private DefaultMQProducer producer;

    public RocketMQProducer(RocketMQProperties properties) {
        this.properties = properties;
    }

    @Override
    public void start() {
        if (!this.running) {
            this.running = true;
            this.producer = new DefaultMQProducer(properties.getGroupName());
            this.producer.setNamesrvAddr(properties.getNameServiceAddr());
            this.producer.setRetryTimesWhenSendFailed(3);
            try {
                this.producer.start();
            }
            catch (MQClientException t) {
                logger.error("error", t);
                throw new RuntimeException("create rocketmq message handler error!", t);
            }
        }
    }

    @Override
    public void stop() {
        if (this.running && this.producer != null) {
            this.producer.shutdown();
        }
        this.running = false;
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    public SendResult handleMessage(String tagName, Message message) throws Exception {
        message.setTopic(properties.getGroupName());
        message.setTags(tagName);
        SendResult sendResult = producer.send(message);
        logger.debug("result is " + JSON.toJSONString(sendResult));
        return sendResult;
    }
}
