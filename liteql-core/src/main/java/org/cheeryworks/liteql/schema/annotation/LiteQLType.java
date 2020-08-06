package org.cheeryworks.liteql.schema.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface LiteQLType {

    String schema();

    String version();

    boolean ignored() default false;

    LiteQLTypeIndex[] indexes() default {};

}
