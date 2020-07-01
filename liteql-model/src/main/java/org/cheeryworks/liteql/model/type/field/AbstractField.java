package org.cheeryworks.liteql.model.type.field;

public abstract class AbstractField implements Field {

    private String name;

    private String type;

    protected AbstractField(String type) {
        this.type = type;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getType() {
        return type;
    }

}
