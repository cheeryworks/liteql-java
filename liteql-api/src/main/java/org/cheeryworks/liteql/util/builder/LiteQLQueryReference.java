package org.cheeryworks.liteql.util.builder;

public class LiteQLQueryReference {

    private String source;

    private String destination;

    public LiteQLQueryReference(String source, String destination) {
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
