package org.flowable.cloud.runtime.core.behavior.classdelegate;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.flowable.cloud.runtime.core.behavior.classdelegate.annotation.CloudClassDelegate;
import org.flowable.cloud.runtime.core.model.CloudContextEntity;
import org.flowable.cloud.runtime.core.utils.NameUtil;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.util.ClassUtils;

/**
 * @author chen.xing<br>
 * @version 1.0<br>
 * @Desc good study day day up
 * @data 2019<br>
 */
public class CloudClassDelegateBeanDefinitionScanner extends ClassPathBeanDefinitionScanner {

    private Class<? extends Annotation> annotationClass;
    private Class<?> markerInterface;

    public CloudClassDelegateBeanDefinitionScanner(BeanDefinitionRegistry registry) {
        super(registry, false);
    }

    public void registerFilters() {
        boolean acceptAllInterfaces = true;

        if (this.annotationClass != null) {
            addIncludeFilter(new AnnotationTypeFilter(this.annotationClass));
            acceptAllInterfaces = false;
        }

        if (this.markerInterface != null) {
            addIncludeFilter(new AssignableTypeFilter(this.markerInterface) {
                @Override
                protected boolean matchClassName(String className) {
                    return false;
                }
            });
            acceptAllInterfaces = false;
        }

        if (acceptAllInterfaces) {
            addIncludeFilter((metadataReader, metadataReaderFactory) -> true);
        }

        addExcludeFilter((metadataReader, metadataReaderFactory) -> metadataReader.getClassMetadata().getClassName().endsWith("package-info"));
    }

    @Override
    protected Set<BeanDefinitionHolder> doScan(String... basePackages) {
        Set<BeanDefinitionHolder> beanDefinitions = super.doScan(basePackages);
        BeanDefinitionRegistry registry = getRegistry();
        List<BeanDefinitionHolder> additionBeanDefinitions = new ArrayList<>(beanDefinitions.size() * 2);
        for (BeanDefinitionHolder beanDefinitionHolder : beanDefinitions) {
            BeanDefinition genericBeanDefinition = beanDefinitionHolder.getBeanDefinition();
            Class<?> genericType;
            try {
                genericType = ClassUtils.forName(genericBeanDefinition.getBeanClassName(), null);
            }
            catch (ClassNotFoundException e) {
                throw new BeanCreationException("can not find class of " + genericBeanDefinition.getBeanClassName(), e);
            }

            CloudClassDelegate cloudClassAnnotation = genericType.getAnnotation(CloudClassDelegate.class);
            String inputChannelBeanName = NameUtil.getInputBeanName(genericType);
            String inputHandlerBeanName = NameUtil.getInputHandler(genericType);
            String outputChannelName = NameUtil.getOutputName(genericType);
            String messageListenerBeanName = NameUtil.getMessageListenerHandler(genericType);
            if (registry.containsBeanDefinition(inputChannelBeanName)) {
                throw new BeanDefinitionStoreException(inputChannelBeanName,
                        "bean definition with this name already exists - " + registry.getBeanDefinition(inputChannelBeanName));
            }
            RootBeanDefinition channelBeanDefinition = new RootBeanDefinition(CloudClassDelegateStreamFactory.class);
            channelBeanDefinition.getConstructorArgumentValues().addGenericArgumentValue(inputChannelBeanName);
            channelBeanDefinition.getConstructorArgumentValues().addGenericArgumentValue(new RuntimeBeanReference(messageListenerBeanName));
            additionBeanDefinitions.add(new BeanDefinitionHolder(channelBeanDefinition, inputChannelBeanName));


            try {
                Method javaDelegeteMethod = genericType.getMethod("execute", DelegateExecution.class);
                RootBeanDefinition delegateHandlerBeanDefinition = new RootBeanDefinition(CloudClassDelegateHandler.class);
                delegateHandlerBeanDefinition.getConstructorArgumentValues().addGenericArgumentValue(new RuntimeBeanReference(beanDefinitionHolder.getBeanName()));
                delegateHandlerBeanDefinition.getConstructorArgumentValues().addGenericArgumentValue(javaDelegeteMethod);
                additionBeanDefinitions.add(new BeanDefinitionHolder(delegateHandlerBeanDefinition, inputHandlerBeanName));


                Method inputHandlerMethod = CloudClassDelegateHandler.class.getMethod("handlerMessage", CloudContextEntity.class);
                RootBeanDefinition messageListenerBeanDefine = new RootBeanDefinition(CloudClassDelegateMessageListener.class);
                messageListenerBeanDefine.getConstructorArgumentValues().addGenericArgumentValue(outputChannelName);
                messageListenerBeanDefine.getConstructorArgumentValues().addGenericArgumentValue(new RuntimeBeanReference(inputHandlerBeanName));
                messageListenerBeanDefine.getConstructorArgumentValues().addGenericArgumentValue(inputHandlerMethod);
                messageListenerBeanDefine.getConstructorArgumentValues().addGenericArgumentValue(cloudClassAnnotation.copyHeader());
                additionBeanDefinitions.add(new BeanDefinitionHolder(messageListenerBeanDefine, messageListenerBeanName));
            }
            catch (NoSuchMethodException e) {
                throw new BeanCreationException("CloudClassDelegateHandler must has method void handlerMessage(IntegrationRequest)", e);
            }
        }
        beanDefinitions.addAll(additionBeanDefinitions);
        additionBeanDefinitions.forEach(beanDefinitionHolder -> registry.registerBeanDefinition(beanDefinitionHolder.getBeanName(), beanDefinitionHolder.getBeanDefinition()));
        return beanDefinitions;
    }

    public void setAnnotationClass(Class<? extends Annotation> annotationClass) {
        this.annotationClass = annotationClass;
    }

    public void setMarkerInterface(Class<?> markerInterface) {
        this.markerInterface = markerInterface;
    }
}
