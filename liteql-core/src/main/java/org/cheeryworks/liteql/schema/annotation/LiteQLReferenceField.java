package org.cheeryworks.liteql.schema.annotation;

import org.cheeryworks.liteql.schema.VoidTraitType;
import org.cheeryworks.liteql.schema.TraitType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface LiteQLReferenceField {

    String name() default "";

    Class<? extends TraitType> targetDomainType();

    Class<? extends TraitType> mappedDomainType() default VoidTraitType.class;

}
