package com.ymatou.dynamicsource.filter;

import com.ymatou.dynamicsource.regist.DbObject;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

import static com.ymatou.dynamicsource.config.Constants.MASTER_PREFIX;
import static com.ymatou.dynamicsource.config.Constants.PATTERN;
import static com.ymatou.dynamicsource.config.Constants.SLAVE_PREFIX;

/**
 * @author 刘志钢
 */
public class CustomFieldFilter extends AbstractFieldFilter {


    public CustomFieldFilter(Object object, Field[] fields, Map<String, DbObject> dbObjectMap) {
        this("custom", object, dbObjectMap, fields);
    }

    CustomFieldFilter(String prefix, Object o, Map<String, DbObject> dbObjectMap, Field[] fields) {
        super(prefix, o, dbObjectMap);
        Map<String, List<Field>> listMap = groupFields(fields);
        listMap.keySet().forEach(key -> dbObjectMap.putIfAbsent(key, new DbObject()));
    }

    @Override
    protected boolean canHandle(Field field) {
        return true;
    }

    @Override
    protected void doFilter(Field field) {
        Matcher matcher = PATTERN.matcher(field.getName());
        if (matcher.matches()) {
            String key = matcher.group(1);
            String writeOrRead = matcher.group(2);

            DbObject dbObject = dbObjectMap.get(key + writeOrRead);
            setFiledValue(field, dbObject, writeOrRead);
        } else {
            throw new IllegalStateException("Field :" + field.getName() + " name is not valid, name " +
                    "must be xxxWrite(Read)Url or something that");
        }
    }

    private Map<String, List<Field>> groupFields(Field[] fields) {
        List<Field> fieldList = Arrays.stream(fields).filter(field -> !field.getName().startsWith(MASTER_PREFIX)
                && !field.getName().startsWith(SLAVE_PREFIX)).collect(Collectors.toList());

        return fieldList.stream().filter(f -> PATTERN.matcher(f.getName()).matches())
                .collect(Collectors.groupingBy(f -> {
                    Matcher matcher = PATTERN.matcher(f.getName());
                    if (matcher.matches()) {
                        String key = matcher.group(1);
                        String writeOrRead = matcher.group(2);
                        return key + writeOrRead; //e.g. mapKey = alphaWrite
                    }
                    throw new IllegalStateException("Field :" + f.getName() + " name is not valid!");
                }, Collectors.toList()));
    }

}
