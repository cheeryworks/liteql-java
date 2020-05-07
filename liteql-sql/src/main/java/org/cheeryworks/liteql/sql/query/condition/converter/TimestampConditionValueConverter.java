package org.cheeryworks.liteql.sql.query.condition.converter;

import com.fasterxml.jackson.databind.util.StdDateFormat;
import org.cheeryworks.liteql.model.query.condition.type.TimestampConditionType;
import org.cheeryworks.liteql.sql.query.condition.ConditionValueConverter;

import java.sql.Timestamp;
import java.text.ParseException;

public class TimestampConditionValueConverter implements ConditionValueConverter<TimestampConditionType> {

    private StdDateFormat stdDateFormat = new StdDateFormat();

    @Override
    public Object convert(TimestampConditionType conditionType, Object value) {
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
