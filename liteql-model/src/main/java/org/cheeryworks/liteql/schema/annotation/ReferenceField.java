package org.cheeryworks.liteql.schema.annotation;

import org.cheeryworks.liteql.model.VoidEntity;
import org.cheeryworks.liteql.model.Entity;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ReferenceField {

    String name() default "";

    Class<? extends Entity> targetDomainType();

    Class<? extends Entity> mappedDomainType() default VoidEntity.class;

}
