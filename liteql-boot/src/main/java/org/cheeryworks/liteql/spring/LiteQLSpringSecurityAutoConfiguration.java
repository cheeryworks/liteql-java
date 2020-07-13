package org.cheeryworks.liteql.spring;


import org.cheeryworks.liteql.boot.LiteQLAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration(proxyBeanMethods = false)
@ConditionalOnBean(WebSecurityConfigurerAdapter.class)
@AutoConfigureAfter(SecurityAutoConfiguration.class)
@AutoConfigureBefore(LiteQLAutoConfiguration.class)
public class LiteQLSpringSecurityAutoConfiguration {

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass(WebSecurityConfigurerAdapter.class)
    @ConditionalOnBean(WebSecurityConfigurerAdapter.class)
    public static class QueryContextHandlerMethodArgumentResolverConfigurer implements WebMvcConfigurer {

        public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
            resolvers.add(new SpringSecurityBasedQueryContextHandlerMethodArgumentResolver());
        }

    }

}
