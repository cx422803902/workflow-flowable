package org.flowable.ui.common.idm.conf;

import java.util.Collections;
import javax.servlet.http.HttpServletResponse;

import org.flowable.ui.common.filter.FlowableCookieFilterRegistrationBean;
import org.flowable.ui.common.idm.properties.FlowableIdmAppProperties;
import org.flowable.ui.common.idm.security.AjaxAuthenticationFailureHandler;
import org.flowable.ui.common.idm.security.AjaxAuthenticationSuccessHandler;
import org.flowable.ui.common.idm.security.CustomerAuthenticationProvider;
import org.flowable.ui.common.properties.FlowableCommonAppProperties;
import org.flowable.ui.common.properties.FlowableRestAppProperties;
import org.flowable.ui.common.security.ActuatorRequestMatcher;
import org.flowable.ui.common.security.ClearFlowableCookieLogoutHandler;
import org.flowable.ui.common.security.DefaultPrivileges;
import org.flowable.ui.common.service.idm.RemoteIdmService;
import org.flowable.ui.common.idm.security.AjaxLogoutSuccessHandler;
import org.flowable.ui.common.idm.security.RemoteIdmAuthenticationProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.actuate.info.InfoEndpoint;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.XXssProtectionHeaderWriter;

/**
 * @author chen.xing<br>
 * @version 1.0<br>
 * @Desc good study day day up
 * @data 2019<br>
 */
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true, jsr250Enabled = true)
public class IdmSecurityConfiguration {
    private static final Logger LOGGER = LoggerFactory.getLogger(IdmSecurityConfiguration.class);

    @Autowired
    protected RemoteIdmAuthenticationProvider authenticationProvider;

    @Autowired
    protected CustomerAuthenticationProvider customerAuthenticationProvider;

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) {

        // Default auth (database backed)
        try {
            auth
                    .authenticationProvider(customerAuthenticationProvider)
                    .authenticationProvider(authenticationProvider);

        }
        catch (Exception e) {
            LOGGER.error("Could not configure authentication mechanism:", e);
        }
    }

    @Bean
    public FlowableCookieFilterRegistrationBean flowableCookieFilterRegistrationBean(RemoteIdmService remoteIdmService, FlowableCommonAppProperties properties) {
        FlowableCookieFilterRegistrationBean filter = new FlowableCookieFilterRegistrationBean(remoteIdmService, properties);
        filter.addUrlPatterns("/app/*");
        filter.setRequiredPrivileges(Collections.singletonList(DefaultPrivileges.ACCESS_MODELER));
        return filter;
    }


    @Configuration
    @Order(1)
    public static class ApiWebSecurityConfigurationAdapter extends WebSecurityConfigurerAdapter {

        protected final FlowableRestAppProperties restAppProperties;

        public ApiWebSecurityConfigurationAdapter(FlowableRestAppProperties restAppProperties) {
            this.restAppProperties = restAppProperties;
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {

            http
                    .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and()
                    .csrf()
                    .disable();

            if (restAppProperties.isRestEnabled()) {

                if (restAppProperties.isVerifyRestApiPrivilege()) {
                    http.antMatcher("/api/**")
                            .authorizeRequests()
                            .antMatchers("/api/**")
                            .hasAuthority(DefaultPrivileges.ACCESS_REST_API).and().httpBasic();
                }
                else {
                    http.antMatcher("/api/**")
                            .authorizeRequests()
                            .antMatchers("/api/**")
                            .authenticated()
                            .and()
                            .httpBasic();

                }

            }
            else {
                http.antMatcher("/api/**")
                        .authorizeRequests()
                        .antMatchers("/api/**")
                        .denyAll();

            }

        }
    }

    @ConditionalOnClass(EndpointRequest.class)
    @Configuration
    @Order(5)
    public static class ActuatorWebSecurityConfigurationAdapter extends WebSecurityConfigurerAdapter {

        @Override
        protected void configure(HttpSecurity http) throws Exception {

            http
                    .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and()
                    .csrf()
                    .disable();

            http
                    .requestMatcher(new ActuatorRequestMatcher())
                    .authorizeRequests()
                    .requestMatchers(EndpointRequest.to(InfoEndpoint.class, HealthEndpoint.class)).authenticated()
                    .requestMatchers(EndpointRequest.toAnyEndpoint()).hasAnyAuthority(DefaultPrivileges.ACCESS_ADMIN)
                    .and().httpBasic();
        }
    }

    @Configuration
    @Order(9) // API config first (has Order(1))
    public static class IdmLoginPageConfigurerAdapter extends WebSecurityConfigurerAdapter {

        @Autowired
        private FlowableIdmAppProperties idmAppProperties;

        @Autowired
        private AjaxAuthenticationSuccessHandler ajaxAuthenticationSuccessHandler;

        @Autowired
        private AjaxAuthenticationFailureHandler ajaxAuthenticationFailureHandler;

        @Autowired
        private AjaxLogoutSuccessHandler ajaxLogoutSuccessHandler;
        @Autowired
        private RememberMeServices rememberMeServices;


        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                    .and()
                    .rememberMe()
                    .rememberMeServices(rememberMeServices)
                    .key(idmAppProperties.getSecurity().getRememberMeKey())

                    .and()
                    .logout()
                    .logoutUrl("/app/logout")
                    .logoutSuccessHandler(ajaxLogoutSuccessHandler)
                    .addLogoutHandler(new ClearFlowableCookieLogoutHandler())
                    .permitAll()

                    .and()
                    .csrf()
                    .disable() // Disabled, cause enabling it will cause sessions
                    .headers()
                    .frameOptions()
                    .sameOrigin()
                    .addHeaderWriter(new XXssProtectionHeaderWriter())

                    .and()
                    .antMatcher("/login/**")
                    .authorizeRequests()
                    .antMatchers("/login/**").permitAll();

            // Custom login form configurer to allow for non-standard HTTP-methods (eg. LOCK)
            FormLoginConfigurer<HttpSecurity> loginConfig = new FormLoginConfigurer<>();
            loginConfig.loginProcessingUrl("/login/authentication")
                    .successHandler(ajaxAuthenticationSuccessHandler)
                    .failureHandler(ajaxAuthenticationFailureHandler)
                    .permitAll();

            http.apply(loginConfig);
        }

    }

    @Configuration
    @Order(10) // API config first (has Order(1))
    public static class FormLoginWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

        @Autowired
        private FlowableCookieFilterRegistrationBean flowableCookieFilterRegistrationBean;
        @Autowired
        private AjaxLogoutSuccessHandler ajaxLogoutSuccessHandler;


        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .exceptionHandling()
                    .authenticationEntryPoint((request, response, authException) -> response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Access Denied"))
                    .and()

                    .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                    .and()
                    .addFilterBefore(flowableCookieFilterRegistrationBean.getFilter(), UsernamePasswordAuthenticationFilter.class)
                    .logout()
                    .logoutUrl("/app/logout")
                    .logoutSuccessHandler(ajaxLogoutSuccessHandler)
                    .addLogoutHandler(new ClearFlowableCookieLogoutHandler())
                    .permitAll()

                    .and()
                    .csrf()
                    .disable() // Disabled, cause enabling it will cause sessions
                    .headers()
                    .frameOptions()
                    .sameOrigin()
                    .addHeaderWriter(new XXssProtectionHeaderWriter())

                    .and()
                    .authorizeRequests()
                    .antMatchers("/app/rest/**").hasAuthority(DefaultPrivileges.ACCESS_MODELER);
        }

    }

}
