package org.cheeryworks.liteql.model.query.save;

import java.util.ArrayList;
import java.util.List;

public class SaveQueryAssociations extends ArrayList<SaveQuery> {

    public SaveQueryAssociations() {
        super();
    }

    public SaveQueryAssociations(List<SaveQuery> saveQueries) {
        super(saveQueries);
    }

}
