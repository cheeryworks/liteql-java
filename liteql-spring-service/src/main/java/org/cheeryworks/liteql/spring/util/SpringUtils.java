package org.cheeryworks.liteql.spring.util;

import org.cheeryworks.liteql.skeleton.enums.Database;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.sql.DatabaseMetaData;
import java.util.Locale;

public class SpringUtils {

    public static Database getDatabase(DataSource dataSource) {
        try {
            String url = JdbcUtils.extractDatabaseMetaData(dataSource, DatabaseMetaData::getURL);

            return getDatabaseFromJdbcUrl(url);
        } catch (Exception ex) {
            throw new IllegalStateException(ex.getMessage(), ex);
        }
    }

    private static Database getDatabaseFromJdbcUrl(String url) {
        if (StringUtils.hasLength(url)) {
            Assert.isTrue(url.startsWith("jdbc"), "URL must start with 'jdbc'");
            String urlWithoutPrefix = url.substring("jdbc".length()).toLowerCase(Locale.ENGLISH);

            if (urlWithoutPrefix.startsWith(Database.H2.name().toLowerCase(Locale.ENGLISH))) {
                return Database.H2;
            } else if (urlWithoutPrefix.startsWith(Database.HSQL.name().toLowerCase(Locale.ENGLISH))) {
                return Database.HSQL;
            } else if (urlWithoutPrefix.startsWith(Database.MYSQL.name().toLowerCase(Locale.ENGLISH))) {
                return Database.MYSQL;
            } else if (urlWithoutPrefix.startsWith(Database.MARIADB.name().toLowerCase(Locale.ENGLISH))) {
                return Database.MARIADB;
            } else if (urlWithoutPrefix.startsWith(Database.POSTGRESQL.name().toLowerCase(Locale.ENGLISH))) {
                return Database.POSTGRESQL;
            }
        }

        throw new IllegalArgumentException("Can not determine database type from url [" + url + "]");
    }

}
