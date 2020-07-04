package org.cheeryworks.liteql;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.cheeryworks.liteql.model.util.LiteQLConstants;
import org.cheeryworks.liteql.model.util.LiteQLJsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

public abstract class AbstractTest {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private final ObjectMapper objectMapper;

    protected Logger getLogger() {
        return logger;
    }

    protected ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public AbstractTest() {
        System.setProperty(LiteQLConstants.DIAGNOSTIC_ENABLED_KEY, Boolean.TRUE.toString());

        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();

        LiteQLJsonUtil.configureObjectMapper(builder);

        objectMapper = builder.createXmlMapper(false).build();
    }

}
