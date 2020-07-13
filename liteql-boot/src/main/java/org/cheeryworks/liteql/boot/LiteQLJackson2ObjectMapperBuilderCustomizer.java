package org.cheeryworks.liteql.boot;

import org.cheeryworks.liteql.model.util.LiteQLJsonUtil;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

public class LiteQLJackson2ObjectMapperBuilderCustomizer implements Jackson2ObjectMapperBuilderCustomizer {

    @Override
    public void customize(Jackson2ObjectMapperBuilder builder) {
        LiteQLJsonUtil.configureObjectMapper(builder);
    }

}
