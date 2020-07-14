package org.cheeryworks.liteql.spring;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.cheeryworks.liteql.model.annotation.Position;
import org.cheeryworks.liteql.model.annotation.ReferenceField;
import org.cheeryworks.liteql.model.annotation.ResourceDefinition;
import org.cheeryworks.liteql.model.annotation.graphql.GraphQLField;
import org.cheeryworks.liteql.model.type.DomainType;
import org.cheeryworks.liteql.model.type.Entity;
import org.cheeryworks.liteql.model.type.Trait;
import org.cheeryworks.liteql.model.type.TraitType;
import org.cheeryworks.liteql.model.type.TypeName;
import org.cheeryworks.liteql.model.type.field.AbstractNullableField;
import org.cheeryworks.liteql.model.type.field.BlobField;
import org.cheeryworks.liteql.model.type.field.BooleanField;
import org.cheeryworks.liteql.model.type.field.ClobField;
import org.cheeryworks.liteql.model.type.field.DecimalField;
import org.cheeryworks.liteql.model.type.field.Field;
import org.cheeryworks.liteql.model.type.field.IdField;
import org.cheeryworks.liteql.model.type.field.IntegerField;
import org.cheeryworks.liteql.model.type.field.StringField;
import org.cheeryworks.liteql.model.type.field.TimestampField;
import org.cheeryworks.liteql.model.type.index.Index;
import org.cheeryworks.liteql.model.type.index.Unique;
import org.cheeryworks.liteql.model.util.ClassUtil;
import org.cheeryworks.liteql.model.util.LiteQLConstants;
import org.cheeryworks.liteql.model.util.LiteQLJsonUtil;
import org.cheeryworks.liteql.model.util.StringUtil;
import org.cheeryworks.liteql.service.Repository;
import org.cheeryworks.liteql.service.util.SqlQueryServiceUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import javax.persistence.Column;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.EntityType;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class DefaultJpaSchemaService implements JpaSchemaService {

    private EntityManagerFactory entityManagerFactory;

    private ObjectMapper objectMapper;

    public DefaultJpaSchemaService(EntityManagerFactory entityManagerFactory, ObjectMapper objectMapper) {
        this.entityManagerFactory = entityManagerFactory;
        this.objectMapper = objectMapper;
    }

    @Override
    public void exportSql(String outputFileAbsolutePath) {
        throw new UnsupportedOperationException();
//        StandardServiceRegistryBuilder registryBuilder = new StandardServiceRegistryBuilder();
//
//        registryBuilder
//                .applySettings(entityManagerFactory.getProperties());
//
//
//        if (StringUtils.isNotEmpty(dialect)) {
//            registryBuilder.applySetting(DIALECT, dialect);
//        }
//
//        MetadataSources metadataSources = new MetadataSources(registryBuilder.build());
//
//        for (ManagedType managedType : entityManagerFactory.getMetamodel().getManagedTypes()) {
//            metadataSources.addAnnotatedClass(managedType.getJavaType());
//        }
//
//        if (StringUtils.isNotEmpty(delimiter)) {
//            delimiter = ";";
//        }
//
//        SchemaExport schemaExport = new SchemaExport()
//                .setHaltOnError(true)
//                .setFormat(true)
//                .setDelimiter(delimiter)
//                .setOutputFile(outputFileAbsolutePath);
//
//        schemaExport.execute(EnumSet.of(STDOUT, SCRIPT), SchemaExport.Action.BOTH, metadataSources.buildMetadata());
    }

    @Override
    public void exportLiteQL(String outputFileAbsolutePath) throws IOException {
        exportLiteQLSchemaAsFiles(outputFileAbsolutePath, getTypeNameWithinSchemas());
    }

    @Override
    public Map<String, Set<TypeName>> getTypeNameWithinSchemas() {
        Map<String, Set<TypeName>> typeNameWithinSchemas = new HashMap<>();

        for (EntityType<?> entityType : entityManagerFactory.getMetamodel().getEntities()) {
            TypeName typeName = Trait.getTypeName(entityType.getJavaType());

            if (typeName != null) {
                Set<TypeName> typeNameWithinSchema = typeNameWithinSchemas.get(typeName.getSchema());

                if (typeNameWithinSchema == null) {
                    typeNameWithinSchema = new LinkedHashSet<>();
                    typeNameWithinSchemas.put(typeName.getSchema(), typeNameWithinSchema);
                }

                DomainType domainType = entityTypeToDomainType(entityType, typeName);

                typeNameWithinSchema.add(domainType);
            }
        }

        ClassPathScanningCandidateComponentProvider scanner =
                new ClassPathScanningCandidateComponentProvider(false) {
                    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
                        AnnotationMetadata metadata = beanDefinition.getMetadata();

                        if (metadata.isInterface()) {
                            return true;
                        }

                        return false;
                    }
                };

        scanner.addIncludeFilter(new AnnotationTypeFilter(ResourceDefinition.class));

        Set<BeanDefinition> beanDefinitions = new HashSet<>();

        for (String packageToScan : LiteQLConstants.getPackageToScan()) {
            beanDefinitions.addAll(scanner.findCandidateComponents(packageToScan));
        }

        for (BeanDefinition beanDefinition : beanDefinitions) {
            Class traitInterface = ClassUtil.getClass(beanDefinition.getBeanClassName());

            if (Entity.class.isAssignableFrom(traitInterface) && traitInterface.isInterface()) {
                TypeName typeName = Trait.getTypeName(traitInterface);

                if (typeName != null) {
                    Set<TypeName> typeNameWithinSchema = typeNameWithinSchemas.get(typeName.getSchema());

                    if (typeNameWithinSchema == null) {
                        typeNameWithinSchema = new LinkedHashSet<>();
                        typeNameWithinSchemas.put(typeName.getSchema(), typeNameWithinSchema);
                    }

                    TraitType traitType = traitInterfaceToTraitType(traitInterface, typeName);

                    typeNameWithinSchema.add(traitType);
                }
            }
        }

        return typeNameWithinSchemas;
    }

    private DomainType entityTypeToDomainType(EntityType<?> entityType, TypeName typeName) {
        DomainType domainType = new DomainType(typeName);

        performFieldsOfDomain(domainType, entityType);

        performUniquesAndIndexesOfDomain(domainType, entityType);

        performTraits(domainType, entityType.getJavaType());

        return domainType;
    }

    private TraitType traitInterfaceToTraitType(Class traitInterface, TypeName typeName) {
        TraitType traitType = new TraitType(typeName);

        performFieldsOfTrait(traitType, traitInterface);

        performTraits(traitType, traitInterface);

        return traitType;
    }

    private void performFieldsOfDomain(DomainType domainType, EntityType<?> entityType) {
        Set<Field> fields = new LinkedHashSet<>();

        List<java.lang.reflect.Field> javaFields = FieldUtils.getAllFieldsList(entityType.getJavaType());

        for (java.lang.reflect.Field javaField : javaFields) {
            if (Modifier.isFinal(javaField.getModifiers()) || Modifier.isStatic(javaField.getModifiers())) {
                continue;
            }

            Attribute attribute = getAttribute(entityType, javaField.getName());

            if (attribute == null) {
                continue;
            }

            Class type = attribute.getJavaType();

            Column column = AnnotationUtils.findAnnotation(javaField, Column.class);

            int length = 255;
            boolean nullable = true;

            if (column != null) {
                if (column.length() > 0) {
                    length = column.length();
                }

                if (!column.nullable()) {
                    nullable = false;
                }
            }

            GraphQLField graphQLField = AnnotationUtils.findAnnotation(javaField, GraphQLField.class);

            boolean isGraphQLField = (graphQLField == null || !graphQLField.ignore()) ? true : false;

            boolean isLobField = AnnotationUtils.findAnnotation(javaField, Lob.class) != null ? true : false;

            ReferenceField referenceField
                    = AnnotationUtils.findAnnotation(javaField, ReferenceField.class);

            Field field = getField(
                    attribute.getName(), type, length, nullable,
                    isGraphQLField, isLobField, referenceField);

            fields.add(field);
        }

        domainType.setFields(fields);
    }

    private Attribute getAttribute(EntityType<?> entityType, String name) {
        try {
            return entityType.getAttribute(name);
        } catch (Exception ex) {
        }

        return null;
    }

    private void performUniquesAndIndexesOfDomain(DomainType domainType, EntityType entityType) {
        Table table = AnnotationUtils.findAnnotation(entityType.getJavaType(), Table.class);

        Set<Unique> uniques = new LinkedHashSet<>();
        Set<Index> indexes = new LinkedHashSet<>();

        if (table != null) {
            for (javax.persistence.Index jpaIndex : table.indexes()) {
                Set<String> columnNames = StringUtil.convertDelimitedParameterToSetOfString(jpaIndex.columnList());

                Set<String> fieldNames = new LinkedHashSet<>();

                for (String columnName : columnNames) {
                    fieldNames.add(SqlQueryServiceUtil.getFieldNameByColumnName(columnName));
                }

                if (jpaIndex.unique()) {
                    Unique unique = new Unique();
                    unique.setFields(fieldNames);

                    uniques.add(unique);
                } else {
                    Index index = new Index();
                    index.setFields(fieldNames);

                    indexes.add(index);
                }
            }
        }

        if (CollectionUtils.isNotEmpty(uniques)) {
            domainType.setUniques(uniques);
        }

        if (CollectionUtils.isNotEmpty(indexes)) {
            domainType.setIndexes(indexes);
        }
    }

    private void performFieldsOfTrait(TraitType traitType, Class traitInterface) {
        Set<Field> fields = new LinkedHashSet<>();

        Method[] methods = traitInterface.getDeclaredMethods();

        Set<Method> sortedMethods = new TreeSet<>((o1, o2) -> {
            Position position1 = o1.getAnnotation(Position.class);
            Position position2 = o2.getAnnotation(Position.class);

            if (position1 != null && position2 != null) {
                return Integer.compare(position1.value(), position2.value());
            }

            return 1;
        });

        sortedMethods.addAll(Arrays.asList(methods));

        for (Method method : sortedMethods) {
            if (Modifier.isPublic(method.getModifiers()) && method.getParameterCount() == 0) {
                Class fieldType = method.getReturnType();

                String name = BeanUtils.findPropertyForMethod(method).getName();

                Field field = getField(name, fieldType, null, true, true, false, null);

                fields.add(field);
            }
        }

        traitType.setFields(fields);
    }

    private Field getField(
            String name, Class fieldType, Integer length, boolean nullable, boolean isGraphQLField,
            boolean isLobField, ReferenceField referenceFieldAnnotation) {
        Field field = null;

        if (IdField.ID_FIELD_NAME.equalsIgnoreCase(name)) {
            IdField idField = new IdField();

            field = idField;
        } else if (fieldType.equals(String.class) && !isLobField && referenceFieldAnnotation == null) {
            StringField stringField = new StringField();

            stringField.setLength(length);

            field = stringField;
        } else if (fieldType.equals(Integer.class) || fieldType.equals(Integer.TYPE)) {
            IntegerField integerField = new IntegerField();

            field = integerField;
        } else if (Date.class.isAssignableFrom(fieldType)) {
            TimestampField timestampField = new TimestampField();

            field = timestampField;
        } else if (fieldType.equals(Boolean.class) || fieldType.equals(Boolean.TYPE)) {
            BooleanField booleanField = new BooleanField();

            field = booleanField;
        } else if (fieldType.equals(BigDecimal.class)) {
            DecimalField decimalField = new DecimalField();

            field = decimalField;
        } else if (fieldType.equals(String.class) && isLobField) {
            ClobField clobField = new ClobField();

            field = clobField;
        } else if (fieldType.equals(Byte[].class) || fieldType.equals(byte[].class)) {
            BlobField blobField = new BlobField();

            field = blobField;
        } else if (fieldType.equals(String.class) && referenceFieldAnnotation != null) {
            org.cheeryworks.liteql.model.type.field.ReferenceField referenceField
                    = new org.cheeryworks.liteql.model.type.field.ReferenceField();

            referenceField.setName(StringUtils.removeEnd(name, "Id"));
            referenceField.setDomainTypeName(Trait.getTypeName(referenceFieldAnnotation.targetDomainType()));

            field = referenceField;
        }

        if (field != null) {
            if (StringUtils.isBlank(field.getName())) {
                field.setName(name);
            }

            if (field instanceof AbstractNullableField && !nullable) {
                ((AbstractNullableField) field).setNullable(false);
            }

            if (!isGraphQLField) {
                field.setGraphQLField(false);
            }
        }

        return field;
    }

    private void performTraits(TraitType traitType, Class javaType) {
        Set<TypeName> typeNames = new LinkedHashSet<>();

        List<Class<?>> javaTypeInterfaces = ClassUtils.getAllInterfaces(javaType);

        Set<Class<?>> traitInterfaces = new TreeSet<>((o1, o2) -> {
            if (o1.isAssignableFrom(o2)) {
                return 0;
            } else {
                return -1;
            }
        });

        for (Class<?> javaTypeInterface : javaTypeInterfaces) {
            if (Entity.class.isAssignableFrom(javaTypeInterface)) {
                traitInterfaces.add(javaTypeInterface);
            }
        }

        for (Class<?> traitInterface : traitInterfaces) {
            TypeName typeName = Trait.getTypeName(traitInterface);

            if (typeName != null) {
                typeNames.add(typeName);
            }
        }

        if (CollectionUtils.isNotEmpty(typeNames)) {
            traitType.setTraits(typeNames);
        }
    }

    private void exportLiteQLSchemaAsFiles(
            String outputFileAbsolutePath, Map<String, Set<TypeName>> domainTypesWithinSchemas) throws IOException {
        File liteQLRoot = new File(outputFileAbsolutePath + "/liteql");

        if (liteQLRoot.exists()) {
            FileUtils.deleteDirectory(liteQLRoot);
        } else {
            liteQLRoot.mkdir();
        }

        for (Map.Entry<String, Set<TypeName>> domainTypesWithinSchema : domainTypesWithinSchemas.entrySet()) {
            File liteQLSchema = new File(
                    liteQLRoot.getPath() + "/" + domainTypesWithinSchema.getKey()
                            + Repository.SUFFIX_OF_SCHEMA_ROOT_FILE);

            FileUtils.write(liteQLSchema, "", StandardCharsets.UTF_8);

            File liteQLSchemaDirectory = new File(
                    liteQLRoot.getPath()
                            + "/" + domainTypesWithinSchema.getKey()
                            + "/" + Repository.NAME_OF_TYPES_DIRECTORY);

            liteQLSchemaDirectory.mkdirs();

            for (TypeName domainTypeName : domainTypesWithinSchema.getValue()) {
                File liteQLModelDirectory = new File(liteQLSchemaDirectory.getPath() + "/" + domainTypeName.getName());

                liteQLModelDirectory.mkdir();

                File liteQLModelDefinition = new File(liteQLModelDirectory + "/" + Repository.NAME_OF_TYPE_DEFINITION);

                FileUtils.write(
                        liteQLModelDefinition, LiteQLJsonUtil.toJson(objectMapper, domainTypeName) + "\n",
                        StandardCharsets.UTF_8);
            }
        }

        FileOutputStream fos = new FileOutputStream(outputFileAbsolutePath + "/liteql.zip");
        ZipOutputStream zipOut = new ZipOutputStream(fos);

        zipFile(liteQLRoot, liteQLRoot.getName(), zipOut);
        zipOut.close();
        fos.close();
    }

    private void zipFile(File fileToZip, String fileName, ZipOutputStream zipOut) throws IOException {
        if (fileToZip.isHidden()) {
            return;
        }

        if (fileToZip.isDirectory()) {
            if (fileName.endsWith("/")) {
                zipOut.putNextEntry(new ZipEntry(fileName));
                zipOut.closeEntry();
            } else {
                zipOut.putNextEntry(new ZipEntry(fileName + "/"));
                zipOut.closeEntry();
            }

            File[] children = fileToZip.listFiles();
            for (File childFile : children) {
                zipFile(childFile, fileName + "/" + childFile.getName(), zipOut);
            }

            return;
        }

        FileInputStream fis = new FileInputStream(fileToZip);
        ZipEntry zipEntry = new ZipEntry(fileName);
        zipOut.putNextEntry(zipEntry);
        byte[] bytes = new byte[1024];
        int length;
        while ((length = fis.read(bytes)) >= 0) {
            zipOut.write(bytes, 0, length);
        }

        fis.close();
    }

}
