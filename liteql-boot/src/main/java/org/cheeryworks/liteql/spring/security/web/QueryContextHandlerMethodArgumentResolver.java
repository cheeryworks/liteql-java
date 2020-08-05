package org.cheeryworks.liteql.spring.security.web;

import org.cheeryworks.liteql.query.DefaultQueryContext;
import org.cheeryworks.liteql.query.QueryContext;
import org.cheeryworks.liteql.model.UserType;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class QueryContextHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {
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
        UserType user = null;

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            if (authentication.getPrincipal() instanceof UserType) {
                user = new SecurityUser((UserType) authentication.getPrincipal());
            } else if (authentication.getPrincipal() instanceof UserDetails) {
                user = new SecurityUser((UserDetails) authentication.getPrincipal());
            }
        }

        return new DefaultQueryContext(user);
    }

}
