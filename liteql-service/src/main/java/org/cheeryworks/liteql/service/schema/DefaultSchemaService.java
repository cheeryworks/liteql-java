package org.cheeryworks.liteql.service.schema;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.cheeryworks.liteql.LiteQLProperties;
import org.cheeryworks.liteql.schema.DomainType;
import org.cheeryworks.liteql.schema.Schema;
import org.cheeryworks.liteql.schema.TraitType;
import org.cheeryworks.liteql.schema.TypeName;
import org.cheeryworks.liteql.schema.annotation.TraitInstance;
import org.cheeryworks.liteql.schema.migration.Migration;
import org.cheeryworks.liteql.service.AbstractLiteQLService;
import org.cheeryworks.liteql.util.LiteQLUtil;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class DefaultSchemaService extends AbstractLiteQLService implements SchemaService {

    private String[] locationPatterns;

    private List<Schema> schemas = new ArrayList<>();

    private Map<Class, Class> traitImplements = initTraitImplements();

    public DefaultSchemaService(LiteQLProperties liteQLProperties, String... locationPatterns) {
        super(liteQLProperties);

        this.locationPatterns = locationPatterns;

        init();
    }

    private void init() {
        Map<String, Map<String, String>> schemaDefinitions = new HashMap<>();

        for (String locationPattern : locationPatterns) {
            try {
                locationPattern = StringUtils.removeEnd(
                        org.springframework.util.StringUtils.cleanPath(locationPattern), "/");

                PathMatchingResourcePatternResolver pathMatchingResourcePatternResolver
                        = new PathMatchingResourcePatternResolver(getClass().getClassLoader());

                Resource[] schemaRootResources
                        = pathMatchingResourcePatternResolver.getResources(locationPattern + "/*.yml");

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
            } catch (IOException ex) {
                throw new IllegalArgumentException(
                        "Location patterns [" + locationPattern + "] invalid, " + ex.getMessage(), ex);
            }
        }

        processSchemaDefinitions(schemaDefinitions);
    }

    private void processSchemaDefinitions(Map<String, Map<String, String>> schemaDefinitions) {
        for (Map.Entry<String, Map<String, String>> schemaDefinition : schemaDefinitions.entrySet()) {
            String schemaName = schemaDefinition.getKey();

            Map<String, String> schemaDefinitionContents = schemaDefinition.getValue();

            for (Map.Entry<String, String> schemaDefinitionContent : schemaDefinitionContents.entrySet()) {
                String schemaDefinitionResourceRelativePath = schemaDefinitionContent.getKey();

                if (!schemaDefinitionResourceRelativePath.contains(SchemaService.NAME_OF_TYPES_DIRECTORY)) {
                    continue;
                }

                TypeName domainTypeName = new TypeName();

                domainTypeName.setSchema(schemaName);
                domainTypeName.setName(schemaDefinitionResourceRelativePath.split("/")[1]);

                if (schemaDefinitionResourceRelativePath.endsWith(NAME_OF_TYPE_DEFINITION)) {
                    TypeName typeName = LiteQLUtil.toBean(
                            schemaDefinitionContent.getValue(), TypeName.class);

                    typeName.setSchema(schemaName);
                    typeName.setName(domainTypeName.getName());

                    addType(typeName);
                }

                if (schemaDefinitionResourceRelativePath.contains("/" + NAME_OF_MIGRATIONS_DIRECTORY + "/")) {
                    String migrationName = schemaDefinitionResourceRelativePath.substring(
                            schemaDefinitionResourceRelativePath.lastIndexOf("/"),
                            schemaDefinitionResourceRelativePath.lastIndexOf("."));
                    Migration migration = LiteQLUtil.toBean(
                            schemaDefinitionContent.getValue(), Migration.class);
                    migration.setName(migrationName);
                    migration.setDomainTypeName(domainTypeName);

                    addMigration(migration);
                }
            }
        }
    }

    public void addType(TypeName typeName) {
        Schema schema = null;

        try {
            schema = getSchema(typeName.getSchema());
        } catch (Exception ex) {
        }

        if (schema == null) {
            schema = new Schema(typeName.getSchema());
            this.schemas.add(schema);
        }

        if (typeName instanceof DomainType) {
            Set<DomainType> domainTypes = schema.getDomainTypes();

            if (domainTypes == null) {
                domainTypes = new TreeSet<>(
                        (o1, o2) -> String.CASE_INSENSITIVE_ORDER.compare(o1.getName(), o2.getName()));

                schema.setDomainTypes(domainTypes);
            }

            domainTypes.add((DomainType) typeName);

        } else if (typeName instanceof TraitType) {
            Set<TraitType> traitTypes = schema.getTraitTypes();

            if (traitTypes == null) {
                traitTypes = new TreeSet<>(
                        (o1, o2) -> String.CASE_INSENSITIVE_ORDER.compare(o1.getName(), o2.getName()));

                schema.setTraitTypes(traitTypes);
            }

            traitTypes.add((TraitType) typeName);
        }
    }

    private void addMigration(Migration migration) {
        Schema schema = getSchema(migration.getDomainTypeName().getSchema());

        Map<TypeName, Map<String, Migration>> migrationsOfSchema = schema.getMigrations();

        if (migrationsOfSchema == null) {
            migrationsOfSchema = new HashMap<>();
            schema.setMigrations(migrationsOfSchema);
        }

        Map<String, Migration> migrations = migrationsOfSchema.get(migration.getDomainTypeName());

        if (migrations == null) {
            migrations = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

            migrationsOfSchema.put(migration.getDomainTypeName(), migrations);
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
                .<IllegalStateException>orElseThrow(() -> {
                    throw new IllegalStateException("Can not get schema [" + schemaName + "]");
                });
    }

    @Override
    public TraitType getTraitType(TypeName typeName) {
        Objects.requireNonNull(typeName, "TypeName is required");

        return getSchema(typeName.getSchema())
                .getTraitTypes()
                .stream()
                .filter(traitType -> traitType.getName().equalsIgnoreCase(typeName.getName()))
                .findFirst()
                .<IllegalStateException>orElseThrow(() -> {
                    throw new IllegalStateException("Can not get trait typeName [" + typeName.getFullname() + "]");
                });
    }

    @Override
    public DomainType getDomainType(TypeName typeName) {
        Objects.requireNonNull(typeName, "TypeName is required");

        return getSchema(typeName.getSchema())
                .getDomainTypes()
                .stream()
                .filter(domainType -> domainType.getName().equalsIgnoreCase(typeName.getName()))
                .findFirst()
                .<IllegalStateException>orElseThrow(() -> {
                    throw new IllegalStateException("Can not get domain typeName [" + typeName.getFullname() + "]");
                });
    }

    @Override
    public Map<TypeName, Map<String, Migration>> getMigrations(String schemaName) {
        return getSchema(schemaName).getMigrations();
    }

    public Map<Class, Class> getTraitImplements() {
        return traitImplements;
    }

    private Map<Class, Class> initTraitImplements() {
        ClassPathScanningCandidateComponentProvider traitInstanceScanner =
                new ClassPathScanningCandidateComponentProvider(false);

        traitInstanceScanner.addIncludeFilter(new AnnotationTypeFilter(TraitInstance.class));

        Set<BeanDefinition> traitInstanceDefinitions = new HashSet<>();

        for (String packageToScan : getLiteQLProperties().getPackagesToScan()) {
            traitInstanceDefinitions.addAll(traitInstanceScanner.findCandidateComponents(packageToScan));
        }

        Map<Class, Class> traitImplements = new HashMap<>();

        for (BeanDefinition traitInstanceDefinition : traitInstanceDefinitions) {
            Class<?> domainJavaType = LiteQLUtil.getClass(traitInstanceDefinition.getBeanClassName());

            TraitInstance traitInstance = domainJavaType.getAnnotation(TraitInstance.class);

            if (!traitInstance.implement().equals(Void.class)) {
                if (traitImplements.containsKey(traitInstance.implement())) {
                    throw new IllegalStateException(
                            "Duplicated implements of"
                                    + " [" + traitInstance.implement().getName() + "]"
                                    + " in different package");
                } else {
                    traitImplements.put(traitInstance.implement(), domainJavaType);
                }
            }
        }

        return Collections.unmodifiableMap(traitImplements);
    }

}
