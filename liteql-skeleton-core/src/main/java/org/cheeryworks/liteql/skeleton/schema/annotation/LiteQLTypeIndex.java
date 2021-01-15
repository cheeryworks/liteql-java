package org.cheeryworks.liteql.skeleton.schema.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({})
@Retention(RUNTIME)
public @interface LiteQLTypeIndex {

    String[] fields();

    boolean unique() default false;

}
