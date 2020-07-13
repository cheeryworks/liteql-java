package org.cheeryworks.liteql.spring;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.cheeryworks.liteql.service.json.AbstractServiceController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;

@RestController
public class JpaSchemaServiceController extends AbstractServiceController {

    @Autowired
    private JpaSchemaService jpaSchemaService;

    @GetMapping(value = "/service/schema/jpa/export/sql")
    public Object exportSql(
            @RequestParam(value = "path", required = false) String path) {
        String schema = "";

        try {
            if (StringUtils.isEmpty(path)) {
                path = "/tmp/schema.sql";
            }

            File file = new File(path);

            if (file.exists()) {
                file.delete();
            }

            jpaSchemaService.exportSql(path);

            FileInputStream inputStream = new FileInputStream(path);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            outputStream.write(IOUtils.toByteArray(inputStream));
            outputStream.flush();
            outputStream.close();

            schema = outputStream.toString();
        } catch (Exception ex) {
            logger.error("SQL schema export failed, " + ex.getMessage(), ex);

            return getErrorResponseEntity(ex);
        }

        return getOkResponseEntity(schema);
    }

    @GetMapping(value = "/service/schema/jpa/export/liteql")
    public Object exportLiteQL(@RequestParam(value = "path", required = false) String path) {

        if (StringUtils.isEmpty(path)) {
            path = "/tmp";
        }

        try {
            jpaSchemaService.exportLiteQL(StringUtils.removeEnd(path, "/"));
        } catch (Exception ex) {
            logger.error("LiteQL schema export failed, " + ex.getMessage(), ex);

            return getErrorResponseEntity(ex);
        }

        return getOkResponseEntity();
    }

}
