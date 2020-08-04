package org.cheeryworks.liteql.schema.annotation;

import org.cheeryworks.liteql.schema.VoidTrait;
import org.cheeryworks.liteql.schema.Trait;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ReferenceField {

    String name() default "";

    Class<? extends Trait> targetDomainType();

    Class<? extends Trait> mappedDomainType() default VoidTrait.class;

}
