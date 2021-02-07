package org.cheeryworks.liteql.skeleton.schema.annotation.field;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@LiteQLField
public @interface LiteQLTimestampField {

    boolean nullable() default true;

}
