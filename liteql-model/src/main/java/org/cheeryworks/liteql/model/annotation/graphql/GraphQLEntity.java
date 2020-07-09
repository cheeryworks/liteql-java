package org.cheeryworks.liteql.model.annotation.graphql;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface GraphQLEntity {

    Class extension() default Void.class;

    Class implement() default Void.class;

}
