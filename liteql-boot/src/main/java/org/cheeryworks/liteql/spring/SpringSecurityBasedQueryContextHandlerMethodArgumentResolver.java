package org.cheeryworks.liteql.spring;

import org.cheeryworks.liteql.model.query.DefaultQueryContext;
import org.cheeryworks.liteql.model.query.QueryContext;
import org.cheeryworks.liteql.model.type.UserEntity;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class SpringSecurityBasedQueryContextHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        if (QueryContext.class.equals(parameter.getParameterType())) {
            return true;
        }

        return false;
    }

    @Override
    public Object resolveArgument(
            MethodParameter parameter, ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        UserEntity user = null;

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            user = new SpringSecurityUser((UserDetails) authentication.getPrincipal());
        }

        return new DefaultQueryContext(user);
    }

}
