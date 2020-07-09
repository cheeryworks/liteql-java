package org.cheeryworks.liteql.model.annotation.graphql;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface GraphQLField {

    String name() default "";

    boolean ignore() default false;

    boolean reference() default false;

}
