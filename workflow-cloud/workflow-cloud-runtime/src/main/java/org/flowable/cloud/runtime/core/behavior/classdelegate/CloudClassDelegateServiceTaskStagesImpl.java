package org.flowable.cloud.runtime.core.behavior.classdelegate;

import java.util.Date;
import java.util.HashMap;

import org.flowable.app.engine.AppEngine;
import org.flowable.cloud.runtime.core.CloudProperties;
import org.flowable.cloud.runtime.core.behavior.classdelegate.annotation.CloudClassDelegate;
import org.flowable.cloud.runtime.core.message.CloudMessageBuilder;
import org.flowable.cloud.runtime.core.model.CloudContextEntity;
import org.flowable.cloud.runtime.core.utils.NameUtil;
import org.flowable.common.engine.impl.context.Context;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.flowable.engine.impl.util.CommandContextUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.cloud.stream.binding.BinderAwareChannelResolver;
import org.springframework.cloud.stream.config.BindingProperties;
import org.springframework.cloud.stream.config.BindingServiceProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.util.ClassUtils;

/**
 * <Description> <br>
 *
 * @author chen.xing01<br>
 * @version 1.0<br>
 */
public class CloudClassDelegateServiceTaskStagesImpl implements CloudClassDelegateServiceTaskStages, SmartInitializingSingleton, ApplicationContextAware {
    private static final Logger logger = LoggerFactory.getLogger(CloudClassDelegateServiceTaskStagesImpl.class);

    @Autowired
    private BinderAwareChannelResolver resolver;
    @Autowired
    private ApplicationEventPublisher eventPublisher;
    @Autowired
    private BinderAwareChannelResolver binderAwareChannelResolver;
    @Autowired
    private BindingServiceProperties bindingServiceProperties;
    @Autowired
    private CloudProperties properties;


    private ApplicationContext applicationContext;
    private RuntimeService runtimeService;
    private AppEngine appEngine;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterSingletonsInstantiated() {
        this.runtimeService = applicationContext.getBean(RuntimeService.class);
        this.appEngine = applicationContext.getBean(AppEngine.class);
        for (BeanDefinitionHolder beanDefinitionHolder : CloudClassDelegateBeanDefinitionRegistrar.getBeanDefinitionHolders()) {
            BeanDefinition beanDefinition = beanDefinitionHolder.getBeanDefinition();
            Class<?> genericType;
            try {
                genericType = ClassUtils.forName(beanDefinition.getBeanClassName(), null);
            }
            catch (ClassNotFoundException e) {
                throw new BeanCreationException("can not find class of " + beanDefinition.getBeanClassName(), e);
            }
            if (JavaDelegate.class.isAssignableFrom(genericType)) {
                CloudClassDelegate cloudClassDelegate = genericType.getAnnotation(CloudClassDelegate.class);
                BindingProperties bindingProperties = new BindingProperties();
                bindingProperties.setDestination(NameUtil.getInputBeanName(genericType));
                bindingProperties.setGroup(properties.getAppName());
                bindingProperties.setContentType(cloudClassDelegate.contentType());
                bindingServiceProperties.setBindings(new HashMap<String, BindingProperties>(1) {{
                    put(NameUtil.getOutputName(genericType), bindingProperties);
                }});
                binderAwareChannelResolver.resolveDestination(NameUtil.getOutputName(genericType));
            }
        }
        binderAwareChannelResolver.resolveDestination(CloudClassDelegateResultHandler.INTEGRATION_RESULTS_PRODUCER);
        CloudClassDelegateBeanDefinitionRegistrar.clearBeanDefinitionHolders();
    }

    @Override
    public void sendCloudRequest(DelegateExecution execution, String className) {
        CloudContextEntity cloudContextEntity = buildCloudContext(execution, className);
        /**
         * {@link #sendCloudRequest}
         */
        eventPublisher.publishEvent(new RequestMessageHold(cloudContextEntity));
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onSendCloudRequest(RequestMessageHold requestMessageHold) {
        MessageChannel messageChannel = resolver.resolveDestination(requestMessageHold.getCloudContextEntity().getClassName());
        messageChannel.send(buildIntegrationRequestMessage(requestMessageHold.getCloudContextEntity()));
    }

    private Message<CloudContextEntity> buildIntegrationRequestMessage(CloudContextEntity cloudContextEntity) {
        return new CloudMessageBuilder()
                .withPayload(cloudContextEntity)
                .build();
    }

    private CloudContextEntity buildCloudContext(DelegateExecution execution, String className) {
        CloudContextEntity integrationContext = new CloudContextEntity();
        integrationContext.setExecutionId(execution.getId());
        integrationContext.setProcessInstanceId(execution.getProcessInstanceId());
        integrationContext.setProcessDefinitionId(execution.getProcessDefinitionId());
        integrationContext.setFlowNodeId(execution.getCurrentActivityId());
        integrationContext.setCreatedDate(new Date());
        integrationContext.setClassName(className);
        return integrationContext;
    }

    @Override
    public void sendCloudResult(DelegateExecution execution, String className) {
        CloudContextEntity cloudContextEntity = new CloudContextEntity();
        cloudContextEntity.setExecutionId(execution.getId());
        cloudContextEntity.setProcessInstanceId(execution.getProcessInstanceId());
        cloudContextEntity.setProcessDefinitionId(execution.getProcessDefinitionId());
        cloudContextEntity.setFlowNodeId(execution.getCurrentActivityId());
        cloudContextEntity.setCreatedDate(new Date());
        cloudContextEntity.setClassName(className);
        eventPublisher.publishEvent(new ResultMessageHold(cloudContextEntity));
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onSendCloudResult(ResultMessageHold resultMessageHold) {
        Message<?> message = MessageBuilder.withPayload(resultMessageHold.getCloudContextEntity()).build();
        MessageChannel messageChannel = this.resolver.resolveDestination(CloudClassDelegateResultHandler.INTEGRATION_RESULTS_PRODUCER);
        messageChannel.send(message);
    }

    @Override
    public void receiveCloudResult(CloudContextEntity contextEntity) {
        appEngine.getAppEngineConfiguration().getCommandExecutor().execute(commandContext -> {
            DelegateExecution execution = CommandContextUtil
                    .getExecutionEntityManager(Context.getCommandContext())
                    .findById(contextEntity.getExecutionId());
            if (execution != null) {
                runtimeService.trigger(execution.getId(), execution.getVariables());
            }
            else {
                String message = "No task is in this RB is waiting for cloud result with execution id `" +
                        execution.getId() +
                        ", flow node id `" + contextEntity.getFlowNodeId() +
                        "`. The integration result for the cloud context `" + contextEntity.getExecutionId() + "` will be ignored.";
                logger.debug(message);
            }
            return null;
        });

    }

    public static class RequestMessageHold {
        private CloudContextEntity cloudContextEntity;

        public RequestMessageHold(CloudContextEntity cloudContextEntity) {
            this.cloudContextEntity = cloudContextEntity;
        }

        public CloudContextEntity getCloudContextEntity() {
            return cloudContextEntity;
        }
    }

    public static class ResultMessageHold {
        private CloudContextEntity cloudContextEntity;

        public ResultMessageHold(CloudContextEntity cloudContextEntity) {
            this.cloudContextEntity = cloudContextEntity;
        }

        public CloudContextEntity getCloudContextEntity() {
            return cloudContextEntity;
        }
    }
}
