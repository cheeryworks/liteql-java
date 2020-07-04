package org.cheeryworks.liteql.model.type;

public interface SortableEntity extends DomainInterface {

    String SORT_CODE_FIELD_NAME = "sortCode";

    String getSortCode();

    void setSortCode(String sortCode);

}
