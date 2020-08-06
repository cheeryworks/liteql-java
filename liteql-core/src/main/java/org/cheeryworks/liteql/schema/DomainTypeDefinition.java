package org.cheeryworks.liteql.schema;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.cheeryworks.liteql.schema.field.Field;
import org.cheeryworks.liteql.schema.field.IdField;
import org.cheeryworks.liteql.schema.field.ReferenceField;
import org.cheeryworks.liteql.schema.index.IndexDefinition;
import org.cheeryworks.liteql.schema.index.UniqueDefinition;

import java.util.Objects;
import java.util.Set;

public class DomainTypeDefinition extends TraitTypeDefinition {

    public static final String DOMAIN_TYPE_NAME_KEY = "domainTypeName";

    private Set<UniqueDefinition> uniques;

    private Set<IndexDefinition> indexes;

    private Boolean graphQLType;

    private Boolean dropped;

    public Set<UniqueDefinition> getUniques() {
        return this.uniques;
    }

    public Set<IndexDefinition> getIndexes() {
        return this.indexes;
    }

    public void setUniques(Set<UniqueDefinition> uniqueDefinitions) {
        this.uniques = uniqueDefinitions;
    }

    public void setIndexes(Set<IndexDefinition> indexes) {
        this.indexes = indexes;
    }

    @JsonGetter
    private Boolean graphQLEntity() {
        return graphQLType;
    }

    @JsonGetter
    private Boolean dropped() {
        return dropped;
    }

    public boolean isGraphQLType() {
        if (graphQLType == null) {
            return true;
        }

        return graphQLType.booleanValue();
    }

    public void setGraphQLType(Boolean graphQLType) {
        this.graphQLType = graphQLType;
    }

    public boolean isDropped() {
        if (dropped == null) {
            return false;
        }

        return dropped.booleanValue();
    }

    public void setDropped(Boolean dropped) {
        this.dropped = dropped;
    }

    public DomainTypeDefinition() {
    }

    public DomainTypeDefinition(TypeName typeName) {
        super(typeName);
    }

    public DomainTypeDefinition(String schema, String name) {
        super(schema, name);
    }

    @Override
    @JsonIgnore
    public boolean isTrait() {
        return false;
    }

    public boolean isReferenceField(String fieldName) {
        if (getFields() != null && getFields().size() > 0) {
            for (Field field : getFields()) {
                if (field instanceof ReferenceField
                        && field.getName().equalsIgnoreCase(fieldName)) {
                    return true;
                }
            }
        }

        return false;
    }

    public ReferenceField getReferenceField(String referenceFieldName) {
        for (Field field : getFields()) {
            if (field instanceof ReferenceField && field.getName().equalsIgnoreCase(referenceFieldName)) {
                return (ReferenceField) field;
            }
        }

        return null;
    }

    public boolean isPersistentDomainType() {
        boolean haveIdField = false;
        boolean havePersistentField = false;

        if (getFields() != null && getFields().size() > 0) {
            for (Field field : getFields()) {
                if (IdField.ID_FIELD_NAME.equalsIgnoreCase(field.getName())) {
                    haveIdField = true;
                } else if (field instanceof ReferenceField) {
                    ReferenceField referenceField = (ReferenceField) field;
                    havePersistentField = !referenceField.isCollection();
                } else {
                    havePersistentField = true;
                }
            }
        }

        return haveIdField && havePersistentField;
    }

    public boolean isGraphQLObjectTypeOnly() {
        boolean haveGraphQLField = false;

        if (getFields() != null && getFields().size() > 0) {
            for (Field field : getFields()) {
                if (field.isGraphQLField()) {
                    haveGraphQLField = true;
                }
            }
        }

        boolean onlyHaveGraphQLField = !isPersistentDomainType();

        return haveGraphQLField && onlyHaveGraphQLField;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof DomainTypeDefinition)) {
            return false;
        }

        if (!super.equals(o)) {
            return false;
        }

        DomainTypeDefinition domainTypeDefinition = (DomainTypeDefinition) o;

        return Objects.equals(uniques, domainTypeDefinition.uniques) &&
                Objects.equals(indexes, domainTypeDefinition.indexes) &&
                Objects.equals(graphQLType, domainTypeDefinition.graphQLType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), uniques, indexes, graphQLType);
    }

}
