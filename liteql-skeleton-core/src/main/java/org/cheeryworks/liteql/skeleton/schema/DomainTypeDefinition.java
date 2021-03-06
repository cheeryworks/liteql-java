package org.cheeryworks.liteql.skeleton.schema;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.cheeryworks.liteql.skeleton.schema.field.Field;
import org.cheeryworks.liteql.skeleton.schema.field.IdField;
import org.cheeryworks.liteql.skeleton.schema.field.ReferenceField;
import org.cheeryworks.liteql.skeleton.schema.index.IndexDefinition;
import org.cheeryworks.liteql.skeleton.schema.index.UniqueDefinition;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

public class DomainTypeDefinition extends TraitTypeDefinition {

    public static final String DOMAIN_TYPE_NAME_KEY = "domainTypeName";

    @JsonDeserialize(as = LinkedHashSet.class)
    private Set<UniqueDefinition> uniques = new HashSet<>();

    @JsonDeserialize(as = LinkedHashSet.class)
    private Set<IndexDefinition> indexes = new HashSet<>();

    @JsonProperty
    private Boolean graphQLType;

    @JsonProperty
    private Boolean dropped;

    private TypeName implementTrait;

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

    public TypeName getImplementTrait() {
        return implementTrait;
    }

    public void setImplementTrait(TypeName implementTrait) {
        this.implementTrait = implementTrait;
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

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        if (!super.equals(o)) {
            return false;
        }

        DomainTypeDefinition that = (DomainTypeDefinition) o;

        return Objects.equals(uniques, that.uniques) &&
                Objects.equals(indexes, that.indexes) &&
                Objects.equals(graphQLType, that.graphQLType) &&
                Objects.equals(dropped, that.dropped) &&
                Objects.equals(implementTrait, that.implementTrait);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), uniques, indexes, graphQLType, dropped, implementTrait);
    }

}
