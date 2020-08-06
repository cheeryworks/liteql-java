package org.cheeryworks.liteql.schema.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface LiteQLField {

    int length() default 255;

    boolean nullable() default true;

    boolean lob() default false;

    boolean ignore() default false;

}
