package org.cheeryworks.liteql.spring.json.query;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.module.jsonSchema.JsonSchemaGenerator;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.cheeryworks.liteql.skeleton.query.AbstractDomainQuery;
import org.cheeryworks.liteql.skeleton.query.AuditQueryContext;
import org.cheeryworks.liteql.skeleton.query.PublicQuery;
import org.cheeryworks.liteql.skeleton.query.Queries;
import org.cheeryworks.liteql.skeleton.query.delete.DeleteQuery;
import org.cheeryworks.liteql.skeleton.query.enums.QueryType;
import org.cheeryworks.liteql.skeleton.query.read.PageReadQuery;
import org.cheeryworks.liteql.skeleton.query.read.ReadQuery;
import org.cheeryworks.liteql.skeleton.query.read.SingleReadQuery;
import org.cheeryworks.liteql.skeleton.query.read.TreeReadQuery;
import org.cheeryworks.liteql.skeleton.query.save.AbstractSaveQuery;
import org.cheeryworks.liteql.skeleton.query.save.SaveQueries;
import org.cheeryworks.liteql.skeleton.schema.TypeDefinition;
import org.cheeryworks.liteql.skeleton.service.query.QueryAccessDecisionService;
import org.cheeryworks.liteql.skeleton.service.query.QueryService;
import org.cheeryworks.liteql.skeleton.service.schema.SchemaService;
import org.cheeryworks.liteql.skeleton.util.LiteQL;
import org.cheeryworks.liteql.spring.json.AbstractServiceController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RestController
public class LiteQLServiceController extends AbstractServiceController {

    private JsonSchemaGenerator schemaGen;

    private SchemaService schemaService;

    private QueryService queryService;

    private QueryAccessDecisionService queryAccessDecisionService;

    @Autowired
    public LiteQLServiceController(
            SchemaService schemaService, QueryService queryService,
            QueryAccessDecisionService queryAccessDecisionService) {
        this.schemaGen = new JsonSchemaGenerator(LiteQL.JacksonJsonUtils.OBJECT_MAPPER);
        this.schemaService = schemaService;
        this.queryService = queryService;
        this.queryAccessDecisionService = queryAccessDecisionService;
    }

    @GetMapping(value = "/liteql/schema")
    public Object getSchemas() {
        try {
            Set<String> schemas = schemaService.getSchemaNames();

            return getOkResponseEntity(schemas);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);

            return getErrorResponseEntity(ex);
        }
    }

    @GetMapping(value = "/liteql/schema/export")
    public void exportSchemas(HttpServletResponse response) {
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment;filename=schemas.zip");
        response.setStatus(HttpServletResponse.SC_OK);

        String schemasPath = schemaService.export();

        File schemasDirectory = new File(schemasPath);

        try {
            ZipOutputStream zipOutputStream = new ZipOutputStream(response.getOutputStream());

            zipSchemas(schemasDirectory, "schemas", zipOutputStream);

            zipOutputStream.close();

            schemasDirectory.delete();
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);

            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void zipSchemas(File file, String fileName, ZipOutputStream zipOutputStream) throws IOException {
        if (file.isFile()) {
            FileInputStream fileInputStream = new FileInputStream(file);

            ZipEntry zipEntry = new ZipEntry(fileName);
            zipOutputStream.putNextEntry(zipEntry);

            IOUtils.copy(fileInputStream, zipOutputStream);

            fileInputStream.close();
        } else {
            zipOutputStream.putNextEntry(new ZipEntry(fileName + "/"));
            zipOutputStream.closeEntry();

            for (File childFile : file.listFiles()) {
                zipSchemas(childFile, fileName + "/" + childFile.getName(), zipOutputStream);
            }
        }
    }

    @GetMapping(value = "/liteql/schema/{schema}/type")
    public Object getTypes(@PathVariable(name = "schema") String schema) {
        try {
            List<TypeDefinition> typeDefinitions = new ArrayList<>();

            typeDefinitions.addAll(schemaService.getTraitTypeDefinitions(schema));
            typeDefinitions.addAll(schemaService.getDomainTypeDefinitions(schema));

            Map<String, TypeDefinition> typeDefinitionsInMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

            for (TypeDefinition typeDefinition : typeDefinitions) {
                typeDefinitionsInMap.put(typeDefinition.getTypeName().getFullname(), typeDefinition);
            }

            return getOkResponseEntity(typeDefinitionsInMap);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);

            return getErrorResponseEntity(ex);
        }
    }

    @GetMapping(value = "/liteql/query/types")
    public Object getQueryTypes() {
        try {
            return getOkResponseEntity(QueryType.values());
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);

            return getErrorResponseEntity(ex);
        }
    }

    @GetMapping(value = "/liteql/query/types/{type}/schema")
    public Object getQueryTypeSchema(@PathVariable(name = "type") String type) {
        try {
            QueryType queryType = QueryType.valueOf(StringUtils.capitalize(type));

            switch (queryType) {
                case Read:
                    return schemaGen.generateSchema(ReadQuery.class);
                case SingleRead:
                    return schemaGen.generateSchema(SingleReadQuery.class);
                case TreeRead:
                    return schemaGen.generateSchema(TreeReadQuery.class);
                case PageRead:
                    return schemaGen.generateSchema(PageReadQuery.class);
                case Create:
                case Update:
                case Delete:
                    return schemaGen.generateSchema(DeleteQuery.class);
                default:
                    throw new UnsupportedOperationException("Query type " + type + " not supported");
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);

            return getErrorResponseEntity(ex);
        }
    }

    @PostMapping(value = "/liteql")
    public Object query(@RequestBody JsonNode liteQL, AuditQueryContext auditQueryContext) {
        try {
            PublicQuery query = LiteQL.JacksonJsonUtils.toBean(liteQL.toString(), PublicQuery.class);

            decide(query, auditQueryContext);

            return queryService.execute(auditQueryContext, query);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);

            return getErrorResponseEntity(ex);
        }
    }

    private void decide(PublicQuery query, AuditQueryContext auditQueryContext) {
        if (query instanceof AbstractDomainQuery) {
            queryAccessDecisionService.decide(auditQueryContext.getUser(), (AbstractDomainQuery) query);
        } else if (query instanceof SaveQueries) {
            for (AbstractSaveQuery saveQuery : (SaveQueries) query) {
                queryAccessDecisionService.decide(auditQueryContext.getUser(), saveQuery);
            }
        } else if (query instanceof Queries) {
            for (PublicQuery subQuery : ((Queries) query).values()) {
                decide(subQuery, auditQueryContext);
            }
        } else {
            throw new IllegalArgumentException("Unsupported query " + query.getClass().getSimpleName());
        }
    }

}
