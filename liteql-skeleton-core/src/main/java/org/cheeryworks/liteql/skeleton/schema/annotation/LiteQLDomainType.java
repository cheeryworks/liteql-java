package org.cheeryworks.liteql.skeleton.schema.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface LiteQLDomainType {

    String schema() default "";

    LiteQLDomainTypeIndex[] indexes() default {};

}
