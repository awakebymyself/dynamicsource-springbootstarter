package com.lzg.dynamicsource.filter;

import com.lzg.dynamicsource.regist.DbObject;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Optional;

import static com.lzg.dynamicsource.config.Constants.*;

/**
 * @author 刘志钢
 */
public abstract class AbstractFieldFilter {

    private final Object object;
    final String prefix;
    protected final Map<String, DbObject> dbObjectMap;

    private AbstractFieldFilter nextFilter;

    AbstractFieldFilter(String prefix, Object o, Map<String, DbObject> dbObjectMap) {
        this.prefix = prefix;
        object = o;
        this.dbObjectMap = dbObjectMap;
    }

    void filter(Field field) {
        if (canHandle(field)) {
            doFilter(field);
        } else if (nextFilter != null) {
            nextFilter.filter(field);
        }
    }

    Optional<String> getFieldValuePerSuffix(Field field, String suffix) {
        if (field.getName().endsWith(suffix)) {
            if (!field.isAccessible()) {
                field.setAccessible(true);
            }
            try {
                return Optional.of((String) field.get(object));
            } catch (IllegalAccessException e) {
                System.out.println("====================");
            }
        }
        return Optional.empty();
    }


    protected abstract boolean canHandle(Field field);

    protected void doFilter(Field field) {
        dbObjectMap.putIfAbsent(prefix, new DbObject());
        DbObject dbObject = dbObjectMap.get(prefix);

        setFiledValue(field, dbObject);
    }

    void setFiledValue(Field field, DbObject dbObject) {
        getFieldValuePerSuffix(field, DRIVER_SUFFIX).ifPresent(dbObject::setDriver);
        getFieldValuePerSuffix(field, PASS_SUFFIX).ifPresent(dbObject::setPassword);
        getFieldValuePerSuffix(field, USER_SUFFIX).ifPresent(dbObject::setUser);
        getFieldValuePerSuffix(field, URL_SUFFIX).ifPresent(dbObject::setUrl);
    }

    void setNextFilter(AbstractFieldFilter nextFilter) {
        this.nextFilter = nextFilter;
    }

}
