package org.cheeryworks.liteql.skeleton.schema.annotation.field;

import org.cheeryworks.liteql.skeleton.schema.field.DecimalField;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@LiteQLField
public @interface LiteQLDecimalField {

    boolean nullable() default true;

    int precision() default DecimalField.DEFAULT_PRECISION;

    int scale() default DecimalField.DEFAULT_SCALE;

}
