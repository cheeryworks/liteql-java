package org.cheeryworks.liteql.jackson;

import org.cheeryworks.liteql.util.LiteQL;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

public class LiteQLJackson2ObjectMapperBuilderCustomizer implements Jackson2ObjectMapperBuilderCustomizer {

    @Override
    public void customize(Jackson2ObjectMapperBuilder builder) {
        builder.modules(LiteQL.JacksonJsonUtils.getLiteQLJsonModule());
    }

}
