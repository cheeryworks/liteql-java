package org.cheeryworks.liteql.service.query.condition.converter;

import com.fasterxml.jackson.databind.util.StdDateFormat;
import org.cheeryworks.liteql.model.enums.ConditionType;
import org.cheeryworks.liteql.service.query.condition.ConditionValueConverter;

import java.sql.Timestamp;
import java.text.ParseException;

public class TimestampConditionValueConverter implements ConditionValueConverter {

    private StdDateFormat stdDateFormat = new StdDateFormat();

    @Override
    public ConditionType getConditionType() {
        return ConditionType.Timestamp;
    }

    @Override
    public Object convert(Object value) {
        try {
            if (value instanceof Timestamp) {
                return value;
            } else {
                if (value instanceof String) {
                    return new Timestamp(stdDateFormat.parse(value.toString()).getTime());
                } else {
                    return new Timestamp((Long) value);
                }
            }
        } catch (ParseException ex) {
            throw new IllegalArgumentException(ex.getMessage(), ex);
        }
    }

}