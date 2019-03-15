package com.workflow.flowable.server.configuration;

import org.flowable.engine.RuntimeService;
import org.flowable.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author chen.xing<br>
 * @version 1.0<br>
 * @Desc good study day day up
 * @data 2019<br>
 */
@Configuration
public class TestConfiguration {
    @Autowired
    private RuntimeService runtimeService;

    @Bean
    public CommandLineRunner runModeleTast() {
        return args -> {
            ProcessInstance instance =  runtimeService.startProcessInstanceByKey("model1");
            System.out.println("this is the instance: " + instance.getId());
        };
    }
}
