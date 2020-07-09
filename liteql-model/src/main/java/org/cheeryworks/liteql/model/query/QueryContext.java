package org.cheeryworks.liteql.model.query;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.cheeryworks.liteql.model.type.UserEntity;

public interface QueryContext {

    UserEntity getUser();

    ObjectMapper getObjectMapper();

}
