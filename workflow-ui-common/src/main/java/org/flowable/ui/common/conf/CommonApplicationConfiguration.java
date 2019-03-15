package org.flowable.ui.common.conf;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author chen.xing<br>
 * @version 1.0<br>
 * @Desc good study day day up
 * @data 2019<br>
 */
@Configuration
@ComponentScan(basePackages = {
        "org.flowable.ui.common.conf",
        "org.flowable.ui.common.filter",
        "org.flowable.ui.common.service",
        "org.flowable.ui.common.repository",
        "org.flowable.ui.common.security",
        "org.flowable.ui.common.tenant"})
public class CommonApplicationConfiguration {
}
