package org.cheeryworks.liteql.service.query;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jsonSchema.JsonSchemaGenerator;
import org.apache.commons.lang3.StringUtils;
import org.cheeryworks.liteql.model.enums.QueryType;
import org.cheeryworks.liteql.model.query.Queries;
import org.cheeryworks.liteql.model.query.QueryContext;
import org.cheeryworks.liteql.model.query.delete.DeleteQuery;
import org.cheeryworks.liteql.model.query.read.PageReadQuery;
import org.cheeryworks.liteql.model.query.read.ReadQuery;
import org.cheeryworks.liteql.model.query.read.SingleReadQuery;
import org.cheeryworks.liteql.model.query.read.TreeReadQuery;
import org.cheeryworks.liteql.model.query.save.AbstractSaveQuery;
import org.cheeryworks.liteql.model.type.TraitType;
import org.cheeryworks.liteql.model.type.TypeName;
import org.cheeryworks.liteql.model.util.LiteQLJsonUtil;
import org.cheeryworks.liteql.model.util.LiteQLUtil;
import org.cheeryworks.liteql.service.QueryService;
import org.cheeryworks.liteql.service.Repository;
import org.cheeryworks.liteql.service.json.AbstractServiceController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

@RestController
public class QueryServiceController extends AbstractServiceController {

    private ObjectMapper objectMapper;

    private JsonSchemaGenerator schemaGen;

    private Repository repository;

    private QueryService queryService;

    @Autowired
    public QueryServiceController(ObjectMapper objectMapper, Repository repository, QueryService queryService) {
        this.objectMapper = objectMapper;
        this.schemaGen = new JsonSchemaGenerator(objectMapper);
        this.repository = repository;
        this.queryService = queryService;
    }

    @GetMapping(value = "/liteql/schema")
    public Object getSchemas() {
        try {
            Set<String> schemas = repository.getSchemaNames();

            return getOkResponseEntity(schemas);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);

            return getErrorResponseEntity(ex);
        }
    }

    @GetMapping(value = "/liteql/schema/{schema}/type")
    public Object getTypes(@PathVariable(name = "schema") String schema) {
        try {
            List<TraitType> types = new ArrayList<>();

            types.addAll(repository.getTraitTypes(schema));
            types.addAll(repository.getDomainTypes(schema));

            Map<String, TraitType> typesInMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

            for (TraitType type : types) {
                typesInMap.put(type.getFullname(), type);
            }

            return getOkResponseEntity(typesInMap);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);

            return getErrorResponseEntity(ex);
        }
    }

    @GetMapping(value = "/liteql/type/{type}")
    public Object getType(@PathVariable(name = "type") String type) {
        try {
            TypeName typeName = LiteQLUtil.getTypeName(type);

            TraitType traitType = repository.getDomainType(typeName);

            if (traitType == null) {
                traitType = repository.getTraitType(typeName);
            }

            return getOkResponseEntity(traitType);
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
                case Save:
                    return schemaGen.generateSchema(AbstractSaveQuery.class);
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
    public Object query(@RequestBody JsonNode liteQL, QueryContext queryContext) {
        try {
            return queryService.execute(
                    queryContext,
                    LiteQLJsonUtil.toBean(objectMapper, liteQL.toString(), Queries.class));
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);

            return getErrorResponseEntity(ex);
        }
    }

}
