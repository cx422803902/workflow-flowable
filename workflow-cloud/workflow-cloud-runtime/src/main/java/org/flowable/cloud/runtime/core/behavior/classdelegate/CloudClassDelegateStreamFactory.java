package org.flowable.cloud.runtime.core.behavior.classdelegate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.binder.Binding;
import org.springframework.cloud.stream.binding.Bindable;
import org.springframework.cloud.stream.binding.BindingService;
import org.springframework.cloud.stream.binding.BindingTargetFactory;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * <Description> <br>
 *
 * @author chen.xing01<br>
 * @version 1.0<br>
 */
public class CloudClassDelegateStreamFactory implements Bindable, InitializingBean, FactoryBean<SubscribableChannel> {

    private SubscribableChannel target;

    @Autowired
    private Map<String, BindingTargetFactory> bindingTargetFactories;

    private String name;

    private MessageHandler messageHandler;

    public CloudClassDelegateStreamFactory(String name, MessageHandler handler) {
        this.name = name;
        this.messageHandler = handler;
    }


    @Override
    public void afterPropertiesSet() {
        Assert.notEmpty(CloudClassDelegateStreamFactory.this.bindingTargetFactories, "'bindingTargetFactories' cannot be empty");
        target = (SubscribableChannel) getBindingTargetFactory(SubscribableChannel.class).createInput(name);
        target.subscribe(messageHandler);
    }

    private BindingTargetFactory getBindingTargetFactory(Class<?> bindingTargetType) {
        List<String> candidateBindingTargetFactories = new ArrayList<>();
        for (Map.Entry<String, BindingTargetFactory> bindingTargetFactoryEntry : this.bindingTargetFactories
                .entrySet()) {
            if (bindingTargetFactoryEntry.getValue().canCreate(bindingTargetType)) {
                candidateBindingTargetFactories.add(bindingTargetFactoryEntry.getKey());
            }
        }
        if (candidateBindingTargetFactories.size() == 1) {
            return this.bindingTargetFactories.get(candidateBindingTargetFactories.get(0));
        }
        else {
            if (candidateBindingTargetFactories.size() == 0) {
                throw new IllegalStateException("No factory found for binding target type: "
                        + bindingTargetType.getName() + " among registered factories: "
                        + StringUtils.collectionToCommaDelimitedString(bindingTargetFactories.keySet()));
            }
            else {
                throw new IllegalStateException(
                        "Multiple factories found for binding target type: " + bindingTargetType.getName() + ": "
                                + StringUtils.collectionToCommaDelimitedString(candidateBindingTargetFactories));
            }
        }
    }

    @Override
    public SubscribableChannel getObject() {
        return target;
    }

    @Override
    public Class<?> getObjectType() {
        return SubscribableChannel.class;
    }

    @Override
    public Collection<Binding<Object>> createAndBindInputs(BindingService bindingService) {
        return bindingService.bindConsumer(target, name);
    }

    @Override
    public void unbindInputs(BindingService bindingService) {
        bindingService.unbindConsumers(name);
    }


    @Override
    public Set<String> getInputs() {
        return Collections.singleton(name);
    }
}