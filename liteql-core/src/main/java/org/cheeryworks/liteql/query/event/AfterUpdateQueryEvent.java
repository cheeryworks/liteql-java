package org.cheeryworks.liteql.query.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.cheeryworks.liteql.query.enums.QueryPhase;
import org.cheeryworks.liteql.query.enums.QueryType;
import org.cheeryworks.liteql.schema.TypeName;

import java.util.List;
import java.util.Map;

public class AfterUpdateQueryEvent extends AbstractWritableQueryEvent {

    @JsonCreator
    public AfterUpdateQueryEvent(
            @JsonProperty("source") List<Map<String, Object>> source,
            @JsonProperty("typeName") TypeName typeName,
            @JsonProperty("queryType") QueryType queryType) {
        super(source, typeName, queryType, QueryPhase.After);
    }

}
