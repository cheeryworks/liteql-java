package org.cheeryworks.liteql.query.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.cheeryworks.liteql.query.QueryContext;
import org.cheeryworks.liteql.query.enums.QueryPhase;
import org.cheeryworks.liteql.query.enums.QueryType;
import org.cheeryworks.liteql.schema.TypeName;

import java.util.List;
import java.util.Map;

public class AfterCreateQueryEvent extends AbstractWritableQueryEvent {

    @JsonCreator
    public AfterCreateQueryEvent(
            @JsonProperty("source") List<Map<String, Object>> source,
            @JsonProperty("typeName") TypeName typeName,
            @JsonProperty("queryType") QueryType queryType,
            @JsonProperty("queryContext") QueryContext queryContext) {
        super(source, typeName, queryType, QueryPhase.After, queryContext);
    }

}
