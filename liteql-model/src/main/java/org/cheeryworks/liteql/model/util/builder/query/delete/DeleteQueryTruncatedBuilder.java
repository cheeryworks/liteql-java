package org.cheeryworks.liteql.model.util.builder.query.delete;

public class DeleteQueryTruncatedBuilder extends DeleteQueryEndBuilder {

    private DeleteQueryMetadata deleteQueryMetadata;

    public DeleteQueryTruncatedBuilder(DeleteQueryMetadata deleteQueryMetadata) {
        super(deleteQueryMetadata);

        this.deleteQueryMetadata = deleteQueryMetadata;
    }

    public DeleteQueryEndBuilder truncated() {
        this.deleteQueryMetadata.setTruncated(true);

        return new DeleteQueryEndBuilder(deleteQueryMetadata);
    }

}
