package org.cheeryworks.liteql.model.query.result;

import java.util.LinkedHashMap;
import java.util.Map;

public class ReadResult extends LinkedHashMap<String, Object> {

    public ReadResult(Map<String, Object> source) {
        super(source);
    }

}
