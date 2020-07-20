package org.cheeryworks.liteql.model.util;

public abstract class ClassUtil {

    public static Class<?> getClass(String className) {
        try {
            return Class.forName(className);
        } catch (Exception ex) {
            throw new IllegalArgumentException(ex.getMessage(), ex);
        }
    }

}
