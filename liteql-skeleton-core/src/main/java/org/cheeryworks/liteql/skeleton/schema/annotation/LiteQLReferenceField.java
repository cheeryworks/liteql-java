package org.cheeryworks.liteql.skeleton.schema.annotation;

import org.cheeryworks.liteql.skeleton.schema.TraitType;
import org.cheeryworks.liteql.skeleton.schema.VoidTraitType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@LiteQLField
public @interface LiteQLReferenceField {

    String name() default "";

    Class<? extends TraitType> targetDomainType();

    Class<? extends TraitType> mappedDomainType() default VoidTraitType.class;

}
