package org.cheeryworks.liteql.model.annotation;

import org.cheeryworks.liteql.model.type.Entity;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ReferenceField {

    String name();

    Class<? extends Entity> targetDomainType();

}
