package org.flowable.cloud.runtime.core.behavior.classdelegate;

import java.lang.reflect.Method;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.config.SpringIntegrationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.integration.handler.AbstractReplyProducingMessageHandler;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.handler.annotation.support.MessageHandlerMethodFactory;
import org.springframework.messaging.handler.invocation.InvocableHandlerMethod;

/**
 * @author chen.xing<br>
 * @version 1.0<br>
 * @Desc good study day day up
 * @data 2019<br>
 */
public class CloudClassDelegateMessageListener extends AbstractReplyProducingMessageHandler {
    private InvocableHandlerMethod invocableHandlerMethod;
    private final Object bean;
    private final Method method;
    private final boolean copyHeaders;


    CloudClassDelegateMessageListener(String outputChannelName, Object bean, Method method, boolean copyHeaders) {
        super();
        this.setOutputChannelName(outputChannelName);
        this.bean = bean;
        this.method = method;
        this.copyHeaders = copyHeaders;
    }

    @Override
    @Autowired
    public void setBeanFactory(BeanFactory beanFactory) {
        super.setBeanFactory(beanFactory);
    }

    @Override
    @Autowired
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        super.setApplicationContext(applicationContext);
    }

    @Autowired
    public void setMessageHandlerMethodFactory(MessageHandlerMethodFactory messageHandlerMethodFactory) {
        this.invocableHandlerMethod = messageHandlerMethodFactory.createInvocableHandlerMethod(bean, method);
    }

    @Autowired
    public void setSpringIntegrationProperties(SpringIntegrationProperties springIntegrationProperties) {
        this.setNotPropagatedHeaders(springIntegrationProperties.getMessageHandlerNotPropagatedHeaders());
    }

    @Override
    protected boolean shouldCopyRequestHeaders() {
        return this.copyHeaders;
    }

    public boolean isVoid() {
        return invocableHandlerMethod.isVoid();
    }

    @Override
    protected Object handleRequestMessage(Message<?> requestMessage) {
        try {
            return this.invocableHandlerMethod.invoke(requestMessage);
        }
        catch (Exception e) {
            if (e instanceof MessagingException) {
                throw (MessagingException) e;
            }
            else {
                throw new MessagingException(requestMessage,
                        "Exception thrown while invoking " + this.invocableHandlerMethod.getShortLogMessage(), e);
            }
        }
    }

}
