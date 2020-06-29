package org.cheeryworks.liteql.util.builder.save;

import org.cheeryworks.liteql.model.query.save.AbstractSaveQuery;

import java.util.Map;

public class LiteQLSaveQueryFieldsBuilder<T extends AbstractSaveQuery> {

    private LiteQLSaveQuery<T> liteQLSaveQuery;

    public LiteQLSaveQueryFieldsBuilder(LiteQLSaveQuery<T> liteQLSaveQuery) {
        this.liteQLSaveQuery = liteQLSaveQuery;
    }

    public LiteQLSaveQueryAssociationsBuilder<T> fields(LiteQLSaveField... liteQLSaveFields) {
        for (LiteQLSaveField liteQLSaveField : liteQLSaveFields) {
            this.liteQLSaveQuery.getData().put(liteQLSaveField.getName(), liteQLSaveField.getValue());
        }

        return new LiteQLSaveQueryAssociationsBuilder<>(this.liteQLSaveQuery);
    }

    public LiteQLSaveQueryAssociationsBuilder<T> fields(Map<String, Object> fields) {
        this.liteQLSaveQuery.getData().putAll(fields);

        return new LiteQLSaveQueryAssociationsBuilder<>(this.liteQLSaveQuery);
    }

}
