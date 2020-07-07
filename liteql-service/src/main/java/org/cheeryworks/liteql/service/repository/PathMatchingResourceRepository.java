package org.cheeryworks.liteql.service.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.cheeryworks.liteql.model.type.DomainType;
import org.cheeryworks.liteql.model.type.Schema;
import org.cheeryworks.liteql.model.type.TraitType;
import org.cheeryworks.liteql.model.type.Type;
import org.cheeryworks.liteql.model.type.migration.Migration;
import org.cheeryworks.liteql.model.util.LiteQLJsonUtil;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.Assert;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class PathMatchingResourceRepository implements Repository {

    private ObjectMapper objectMapper;

    private String[] locationPatterns;

    private List<Schema> schemas = new ArrayList<>();

    public PathMatchingResourceRepository(ObjectMapper objectMapper, String... locationPatterns) {
        this.objectMapper = objectMapper;
        this.locationPatterns = locationPatterns;

        init();
    }

    private void init() {

        for (String locationPattern : locationPatterns) {
            try {
                locationPattern = StringUtils.removeEnd(
                        org.springframework.util.StringUtils.cleanPath(locationPattern), "/");

                PathMatchingResourcePatternResolver pathMatchingResourcePatternResolver
                        = new PathMatchingResourcePatternResolver(getClass().getClassLoader());

                Resource[] schemaRootResources
                        = pathMatchingResourcePatternResolver.getResources(locationPattern + "/*.yml");

                Map<String, Map<String, String>> schemaDefinitions = new HashMap<>();

                Map<String, String> schemaPaths = new HashMap<>();

                for (Resource schemaRootResource : schemaRootResources) {
                    String schemaName = schemaRootResource.getFilename()
                            .substring(0, schemaRootResource.getFilename().lastIndexOf("."));
                    String schemaRootResourcePath = schemaRootResource.getURL().getPath()
                            .substring(0, schemaRootResource.getURL().getPath().lastIndexOf("."));

                    if (schemaDefinitions.get(schemaName) == null) {
                        schemaDefinitions.put(schemaName, new HashMap<>());
                        schemaPaths.put(schemaName, schemaRootResourcePath);
                    } else {
                        throw new IllegalArgumentException("Schema [" + schemaName + "]"
                                + " exist in path [" + schemaPaths.get(schemaName) + "]"
                                + ", but find in another path [" + schemaRootResourcePath + "]");
                    }
                }

                Resource[] schemaDefinitionResources
                        = pathMatchingResourcePatternResolver.getResources(locationPattern + "/**/*.json");

                for (Resource schemaDefinitionResource : schemaDefinitionResources) {
                    String schemaDefinitionResourcePath = schemaDefinitionResource.getURL().getPath();

                    for (Map.Entry<String, String> schemaPathEntry : schemaPaths.entrySet()) {
                        if (schemaDefinitionResourcePath.startsWith(schemaPathEntry.getValue())) {
                            String schemaDefinitionResourceRelativePath
                                    = schemaDefinitionResourcePath.substring(schemaPathEntry.getValue().length() + 1);
                            schemaDefinitions.get(schemaPathEntry.getKey()).put(
                                    schemaDefinitionResourceRelativePath,
                                    IOUtils.toString(
                                            schemaDefinitionResource.getInputStream(), StandardCharsets.UTF_8));
                            break;
                        }
                    }
                }

                for (Map.Entry<String, Map<String, String>> schemaDefinition : schemaDefinitions.entrySet()) {
                    String schemaName = schemaDefinition.getKey();

                    Map<String, String> schemaDefinitionContents = schemaDefinition.getValue();

                    for (Map.Entry<String, String> schemaDefinitionContent : schemaDefinitionContents.entrySet()) {
                        String schemaDefinitionResourceRelativePath = schemaDefinitionContent.getKey();

                        if (!schemaDefinitionResourceRelativePath.contains(Repository.NAME_OF_TYPES_DIRECTORY)) {
                            continue;
                        }

                        Type domainType = new Type();

                        domainType.setSchema(schemaName);
                        domainType.setName(schemaDefinitionResourceRelativePath.split("/")[1]);

                        if (schemaDefinitionResourceRelativePath.endsWith(NAME_OF_TYPE_DEFINITION)) {
                            Type type = LiteQLJsonUtil.toBean(
                                    objectMapper, schemaDefinitionContent.getValue(), Type.class);

                            type.setSchema(schemaName);
                            type.setName(domainType.getName());

                            addType(type);
                        }

                        if (schemaDefinitionResourceRelativePath.contains("/" + NAME_OF_MIGRATIONS_DIRECTORY + "/")) {
                            String migrationName = schemaDefinitionResourceRelativePath.substring(
                                    schemaDefinitionResourceRelativePath.lastIndexOf("/"),
                                    schemaDefinitionResourceRelativePath.lastIndexOf("."));
                            Migration migration = LiteQLJsonUtil.toBean(
                                    objectMapper, schemaDefinitionContent.getValue(), Migration.class);
                            migration.setName(migrationName);
                            migration.setDomainType(domainType);

                            addMigration(migration);
                        }
                    }
                }
            } catch (IOException ex) {
                throw new IllegalArgumentException(
                        "Location patterns [" + locationPattern + "] invalid, " + ex.getMessage(), ex);
            }
        }
    }

    protected void addType(Type type) {
        Schema schema = null;

        try {
            schema = getSchema(type.getSchema());
        } catch (Exception ex) {
        }

        if (schema == null) {
            schema = new Schema(type.getSchema());
            this.schemas.add(schema);
        }

        if (type instanceof DomainType) {
            Set<DomainType> domainTypes = schema.getDomainTypes();

            if (domainTypes == null) {
                domainTypes = new TreeSet<>(
                        (o1, o2) -> String.CASE_INSENSITIVE_ORDER.compare(o1.getName(), o2.getName()));

                schema.setDomainTypes(domainTypes);
            }

            domainTypes.add((DomainType) type);

        } else if (type instanceof TraitType) {
            Set<TraitType> traitTypes = schema.getTraitTypes();

            if (traitTypes == null) {
                traitTypes = new TreeSet<>(
                        (o1, o2) -> String.CASE_INSENSITIVE_ORDER.compare(o1.getName(), o2.getName()));

                schema.setTraitTypes(traitTypes);
            }

            traitTypes.add((TraitType) type);
        }
    }

    private void addMigration(Migration migration) {
        Schema schema = getSchema(migration.getDomainType().getSchema());

        Map<Type, Map<String, Migration>> migrationsOfSchema = schema.getMigrations();

        if (migrationsOfSchema == null) {
            migrationsOfSchema = new HashMap<>();
            schema.setMigrations(migrationsOfSchema);
        }

        Map<String, Migration> migrations = migrationsOfSchema.get(migration.getDomainType());

        if (migrations == null) {
            migrations = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        }

        migrations.put(migration.getName(), migration);
    }

    @Override
    public Set<String> getSchemaNames() {
        Set<String> schemaNames = this.schemas
                .stream()
                .map(schema -> schema.getName())
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        return schemaNames;
    }

    @Override
    public Set<TraitType> getTraitTypes(String schemaName) {
        return Optional.ofNullable(getSchema(schemaName).getTraitTypes()).orElse(Collections.EMPTY_SET);
    }

    @Override
    public Set<DomainType> getDomainTypes(String schemaName) {
        return Optional.ofNullable(getSchema(schemaName).getDomainTypes()).orElse(Collections.EMPTY_SET);
    }

    private Schema getSchema(String schemaName) {
        return this.schemas
                .stream()
                .filter(schema -> schema.getName().equalsIgnoreCase(schemaName))
                .findFirst()
                .orElseThrow(() -> {
                    throw new IllegalStateException("Can not get schema [" + schemaName + "]");
                });
    }

    @Override
    public TraitType getTraitType(Type type) {
        Assert.notNull(type, "Type is required");

        return getSchema(type.getSchema())
                .getTraitTypes()
                .stream()
                .filter(traitType -> traitType.getName().equalsIgnoreCase(type.getName()))
                .findFirst()
                .orElseThrow(() -> {
                    throw new IllegalStateException("Can not get trait type [" + type.getFullname() + "]");
                });
    }

    @Override
    public DomainType getDomainType(Type type) {
        Assert.notNull(type, "Type is required");

        return getSchema(type.getSchema())
                .getDomainTypes()
                .stream()
                .filter(domainType -> domainType.getName().equalsIgnoreCase(type.getName()))
                .findFirst()
                .orElseThrow(() -> {
                    throw new IllegalStateException("Can not get domain type [" + type.getFullname() + "]");
                });
    }

    @Override
    public List<Migration> getMigrations(String schemaName) {
        List<Migration> migrations = new ArrayList<>();

        Schema schema = getSchema(schemaName);

        if (MapUtils.isNotEmpty(schema.getMigrations())) {
            for (Map.Entry<Type, Map<String, Migration>> migrationOfDomainType
                    : schema.getMigrations().entrySet()) {
                migrations.addAll(migrationOfDomainType.getValue().values());
            }
        }

        return Collections.unmodifiableList(migrations);
    }

}
