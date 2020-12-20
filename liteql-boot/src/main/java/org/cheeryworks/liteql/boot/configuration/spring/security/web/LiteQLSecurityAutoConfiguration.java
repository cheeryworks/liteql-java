package org.cheeryworks.liteql.boot.configuration.spring.security.web;


import org.cheeryworks.liteql.spring.security.web.QueryContextHandlerMethodArgumentResolver;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration(proxyBeanMethods = false)
@AutoConfigureBefore(SecurityAutoConfiguration.class)
public class LiteQLSecurityAutoConfiguration {

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnMissingBean(WebSecurityConfigurerAdapter.class)
    public static class BasicAuthConfiguration extends WebSecurityConfigurerAdapter {

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.csrf().disable()
                    .authorizeRequests().anyRequest().authenticated()
                    .and()
                    .httpBasic();
        }

    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass({
            WebMvcConfigurer.class,
            Authentication.class
    })
    class QueryContextHandlerMethodArgumentResolverConfigurer implements WebMvcConfigurer {

        public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
            resolvers.add(new QueryContextHandlerMethodArgumentResolver());
        }

    }

}
