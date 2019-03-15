package org.flowable.cloud.runtime.core.behavior.classdelegate.annotation;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.flowable.cloud.runtime.core.behavior.classdelegate.CloudClassDelegateBeanDefinitionRegistrar;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Import;
import org.springframework.integration.config.EnableIntegration;

/**
 * @author chen.xing<br>
 * @version 1.0<br>
 * @Desc good study day day up
 * @data 2019<br>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@EnableIntegration
@EnableDiscoveryClient
@Import(CloudClassDelegateBeanDefinitionRegistrar.class)
public @interface CloudClassDelegateScan {

    String[] value() default {};

    String[] basePackages() default {};

    Class<?>[] basePackageClasses() default {};

    Class<? extends Annotation> annotationClass() default Annotation.class;

    Class<?> markerInterface() default Class.class;
}
