package org.cheeryworks.liteql.schema.annotation;

import org.cheeryworks.liteql.schema.TraitType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface LiteQLStaticType {

    Class<? extends TraitType> value();

}
