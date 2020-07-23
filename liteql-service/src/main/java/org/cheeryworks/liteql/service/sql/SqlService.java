package org.cheeryworks.liteql.service.sql;

import org.cheeryworks.liteql.service.LiteQLService;

public interface SqlService extends LiteQLService {

    SqlCustomizer getSqlCustomizer();

}
