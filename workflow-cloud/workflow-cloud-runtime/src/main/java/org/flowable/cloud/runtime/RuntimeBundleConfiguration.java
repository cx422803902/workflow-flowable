package org.flowable.cloud.runtime;

import java.io.IOException;
import java.util.List;
import javax.sql.DataSource;

import org.flowable.cloud.runtime.core.CloudProperties;
import org.flowable.cloud.runtime.core.Constant;
import org.flowable.cloud.runtime.core.behavior.classdelegate.CloudClassDelegateFactory;
import org.flowable.cloud.runtime.core.behavior.classdelegate.CloudClassDelegateResultHandler;
import org.flowable.cloud.runtime.core.behavior.classdelegate.CloudClassDelegateServiceTaskStages;
import org.flowable.cloud.runtime.core.behavior.classdelegate.CloudClassDelegateServiceTaskStagesImpl;
import org.flowable.cloud.runtime.core.behavior.classdelegate.annotation.CloudClassDelegate;
import org.flowable.cloud.runtime.core.behavior.classdelegate.annotation.CloudClassDelegateScan;
import org.flowable.common.engine.impl.cfg.IdGenerator;
import org.flowable.common.engine.impl.persistence.StrongUuidGenerator;
import org.flowable.engine.delegate.JavaDelegate;
import org.flowable.engine.impl.bpmn.parser.factory.DefaultActivityBehaviorFactory;
import org.flowable.job.service.impl.asyncexecutor.AsyncExecutor;
import org.flowable.spring.SpringProcessEngineConfiguration;
import org.flowable.spring.boot.AbstractSpringEngineAutoConfiguration;
import org.flowable.spring.boot.FlowableMailProperties;
import org.flowable.spring.boot.FlowableProperties;
import org.flowable.spring.boot.app.FlowableAppProperties;
import org.flowable.spring.boot.idm.FlowableIdmProperties;
import org.flowable.spring.boot.process.FlowableProcessProperties;
import org.flowable.spring.boot.process.Process;
import org.flowable.spring.boot.process.ProcessAsync;
import org.flowable.spring.boot.process.ProcessAsyncHistory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.transaction.PlatformTransactionManager;


/**
 * <Description> <br>
 *
 * @author chen.xing01<br>
 * @version 1.0<br>
 */
@Configuration
@PropertySource("classpath:/application-runtime-bundle.properties")
@CloudClassDelegateScan(
        basePackages = ("com.workflow.flowable"),
        annotationClass = CloudClassDelegate.class,
        markerInterface = JavaDelegate.class
)
@EnableBinding(CloudClassDelegateResultHandler.ProcessResultChannels.class)
public class RuntimeBundleConfiguration extends AbstractSpringEngineAutoConfiguration implements ApplicationContextAware {

    protected final FlowableProcessProperties processProperties;
    protected final FlowableAppProperties appProperties;
    protected final FlowableIdmProperties idmProperties;
    protected final FlowableMailProperties mailProperties;
    private ApplicationContext applicationContext;
    @Autowired
    public RuntimeBundleConfiguration(FlowableProperties flowableProperties, FlowableProcessProperties processProperties,
                                      FlowableAppProperties appProperties, FlowableIdmProperties idmProperties, FlowableMailProperties mailProperties) {
        super(flowableProperties);
        this.processProperties = processProperties;
        this.appProperties = appProperties;
        this.idmProperties = idmProperties;
        this.mailProperties = mailProperties;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Bean
    @ConfigurationProperties(prefix = "spring.cloud.runtime-bundle")
    public CloudProperties cloudProperties() {
        return new CloudProperties();
    }

    @Bean
    public CloudClassDelegateResultHandler cloudClassDelegateResultHandler() {
        return new CloudClassDelegateResultHandler();
    }

    @Bean(Constant.CLOUD_STAGE_BEAN_NAME)
    public CloudClassDelegateServiceTaskStages cloudClassDelegateServiceTaskStages() {
        return new CloudClassDelegateServiceTaskStagesImpl();
    }

    @Bean
    public SpringProcessEngineConfiguration springProcessEngineConfiguration(DataSource dataSource, PlatformTransactionManager platformTransactionManager,
                                                                             @Process ObjectProvider<IdGenerator> processIdGenerator,
                                                                             ObjectProvider<IdGenerator> globalIdGenerator,
                                                                             @ProcessAsync ObjectProvider<AsyncExecutor> asyncExecutorProvider,
                                                                             @ProcessAsyncHistory ObjectProvider<AsyncExecutor> asyncHistoryExecutorProvider,
                                                                             @Qualifier(value = Constant.CLOUD_STAGE_BEAN_NAME) CloudClassDelegateServiceTaskStages classDelegateServiceTaskStages) throws IOException {

        SpringProcessEngineConfiguration conf = new SpringProcessEngineConfiguration();

        List<Resource> resources = this.discoverDeploymentResources(
                flowableProperties.getProcessDefinitionLocationPrefix(),
                flowableProperties.getProcessDefinitionLocationSuffixes(),
                flowableProperties.isCheckProcessDefinitions()
        );

        if (resources != null && !resources.isEmpty()) {
            conf.setDeploymentResources(resources.toArray(new Resource[0]));
            conf.setDeploymentName(flowableProperties.getDeploymentName());
        }

        AsyncExecutor springAsyncExecutor = asyncExecutorProvider.getIfUnique();
        if (springAsyncExecutor != null) {
            conf.setAsyncExecutor(springAsyncExecutor);
        }

        AsyncExecutor springAsyncHistoryExecutor = asyncHistoryExecutorProvider.getIfUnique();
        if (springAsyncHistoryExecutor != null) {
            conf.setAsyncHistoryEnabled(true);
            conf.setAsyncHistoryExecutor(springAsyncHistoryExecutor);
        }
        configureSpringEngine(conf, platformTransactionManager);
        configureEngine(conf, dataSource);

        conf.setDeploymentName(defaultText(flowableProperties.getDeploymentName(), conf.getDeploymentName()));

        conf.setDisableIdmEngine(!(flowableProperties.isDbIdentityUsed() && idmProperties.isEnabled()));

        conf.setAsyncExecutorActivate(flowableProperties.isAsyncExecutorActivate());
        conf.setAsyncHistoryExecutorActivate(flowableProperties.isAsyncHistoryExecutorActivate());

        conf.setMailServerHost(mailProperties.getHost());
        conf.setMailServerPort(mailProperties.getPort());
        conf.setMailServerUsername(mailProperties.getUsername());
        conf.setMailServerPassword(mailProperties.getPassword());
        conf.setMailServerDefaultFrom(mailProperties.getDefaultFrom());
        conf.setMailServerForceTo(mailProperties.getForceTo());
        conf.setMailServerUseSSL(mailProperties.isUseSsl());
        conf.setMailServerUseTLS(mailProperties.isUseTls());

        conf.setEnableProcessDefinitionHistoryLevel(processProperties.isEnableProcessDefinitionHistoryLevel());
        conf.setProcessDefinitionCacheLimit(processProperties.getDefinitionCacheLimit());
        conf.setEnableSafeBpmnXml(processProperties.isEnableSafeXml());

        conf.setHistoryLevel(flowableProperties.getHistoryLevel());

        IdGenerator idGenerator = getIfAvailable(processIdGenerator, globalIdGenerator);
        if (idGenerator == null) {
            idGenerator = new StrongUuidGenerator();
        }
        conf.setIdGenerator(idGenerator);
        conf.setActivityBehaviorFactory(new DefaultActivityBehaviorFactory(new CloudClassDelegateFactory(applicationContext, classDelegateServiceTaskStages)));
        return conf;
    }
}
