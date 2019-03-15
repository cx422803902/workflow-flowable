package org.flowable.cloud.runtime.core.behavior.classdelegate;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.flowable.cloud.runtime.core.behavior.classdelegate.annotation.CloudClassDelegateScan;
import org.flowable.cloud.runtime.core.utils.NameUtil;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

/**
 * <Description> <br>
 *
 * @author chen.xing01<br>
 * @version 1.0<br>
 */
public class CloudClassDelegateBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar {


    private static Set<BeanDefinitionHolder> beanDefinitionHolders = new HashSet<>();


    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata,
                                        BeanDefinitionRegistry registry) {

        AnnotationAttributes annoAttrs = AnnotationAttributes.fromMap(importingClassMetadata.getAnnotationAttributes(CloudClassDelegateScan.class.getName()));
        CloudClassDelegateBeanDefinitionScanner scanner = new CloudClassDelegateBeanDefinitionScanner(registry);

        Class<? extends Annotation> annotationClass = annoAttrs.getClass("annotationClass");
        if (!Annotation.class.equals(annotationClass)) {
            scanner.setAnnotationClass(annotationClass);
        }

        Class<?> markerInterface = annoAttrs.getClass("markerInterface");
        if (!Class.class.equals(markerInterface)) {
            scanner.setMarkerInterface(markerInterface);
        }

        List<String> basePackages = new ArrayList<String>();
        for (String pkg : annoAttrs.getStringArray("value")) {
            if (StringUtils.hasText(pkg)) {
                basePackages.add(pkg);
            }
        }
        for (String pkg : annoAttrs.getStringArray("basePackages")) {
            if (StringUtils.hasText(pkg)) {
                basePackages.add(pkg);
            }
        }
        for (Class<?> clazz : annoAttrs.getClassArray("basePackageClasses")) {
            basePackages.add(ClassUtils.getPackageName(clazz));
        }
        scanner.registerFilters();
        scanner.setBeanNameGenerator((definition, definitionRegistry) -> NameUtil.getJavaDelegateBeanName(definition.getBeanClassName()));
        beanDefinitionHolders.addAll(scanner.doScan(StringUtils.toStringArray(basePackages)));
    }

    public static Set<BeanDefinitionHolder> getBeanDefinitionHolders() {
        return beanDefinitionHolders;
    }

    public static void clearBeanDefinitionHolders() {
        beanDefinitionHolders.clear();
    }
}
