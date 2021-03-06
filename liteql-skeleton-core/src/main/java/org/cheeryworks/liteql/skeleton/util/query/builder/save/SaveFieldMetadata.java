package org.cheeryworks.liteql.skeleton.util.query.builder.save;

public class SaveFieldMetadata {

    private String name;

    private Object value;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public SaveFieldMetadata(String name, Object value) {
        this.name = name;
        this.value = value;
    }

}
