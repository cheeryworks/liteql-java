package org.cheeryworks.liteql.skeleton.util.query.builder;

public class QueryReference {

    private String source;

    private String destination;

    public QueryReference(String source, String destination) {
        this.source = source;
        this.destination = destination;
    }

    public String getSource() {
        return source;
    }

    public String getDestination() {
        return destination;
    }

}
