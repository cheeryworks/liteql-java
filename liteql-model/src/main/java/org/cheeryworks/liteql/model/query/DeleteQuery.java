package org.cheeryworks.liteql.model.query;

public class DeleteQuery extends AbstractConditionalQuery {

    private boolean truncated;

    public boolean isTruncated() {
        return truncated;
    }

    public void setTruncated(boolean truncated) {
        this.truncated = truncated;
    }

}
