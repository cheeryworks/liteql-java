package org.cheeryworks.liteql.query.save;

import java.util.ArrayList;
import java.util.List;

public class SaveQueryAssociations extends ArrayList<AbstractSaveQuery> {

    public SaveQueryAssociations() {
        super();
    }

    public SaveQueryAssociations(List<AbstractSaveQuery> saveQueries) {
        super(saveQueries);
    }

}
