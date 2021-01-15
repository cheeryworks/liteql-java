package org.cheeryworks.liteql.skeleton.query.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.cheeryworks.liteql.skeleton.query.QueryContext;
import org.cheeryworks.liteql.skeleton.query.enums.QueryPhase;
import org.cheeryworks.liteql.skeleton.query.enums.QueryType;
import org.cheeryworks.liteql.skeleton.schema.TypeName;

import java.util.List;
import java.util.Map;

public class BeforeCreateQueryEvent extends AbstractWritableQueryEvent {

    @JsonCreator
    public BeforeCreateQueryEvent(
            @JsonProperty("source") List<Map<String, Object>> source,
            @JsonProperty("typeName") TypeName typeName,
            @JsonProperty("queryType") QueryType queryType,
            @JsonProperty("queryContext") QueryContext queryContext) {
        super(source, typeName, queryType, QueryPhase.Before, queryContext);
    }

}
