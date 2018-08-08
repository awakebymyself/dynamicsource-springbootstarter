package com.lzg.dynamicsource.filter;

import com.lzg.dynamicsource.regist.DbObject;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * @author 刘志钢
 */
public abstract class AbstractFieldFilter {

    private static final String URL_SUFFIX = "Url";
    private static final String USER_SUFFIX = "User";
    private static final String PASS_SUFFIX = "Password";
    private static final String DRIVER_SUFFIX = "Driver";

    private final Object object;
    final String prefix;
    private final Map<String, DbObject> dbObjectMap;

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

    String getFieldValuePerSuffix(Field field, String suffix) {
        if (field.getName().endsWith(suffix)) {
            if (!field.isAccessible()) {
                field.setAccessible(true);
            }
            try {
                return (String) field.get(object);
            } catch (IllegalAccessException e) {
                System.out.println("====================");
            }
        }
        return null;
    }


    protected abstract boolean canHandle(Field field);

    protected void doFilter(Field field) {
        dbObjectMap.putIfAbsent(prefix, new DbObject());
        DbObject dbObject = dbObjectMap.get(prefix);

        dbObject.setDriver(getFieldValuePerSuffix(field, DRIVER_SUFFIX));
        dbObject.setPassword(getFieldValuePerSuffix(field, PASS_SUFFIX));
        dbObject.setUser(getFieldValuePerSuffix(field, USER_SUFFIX));
        dbObject.setUrl(getFieldValuePerSuffix(field, URL_SUFFIX));
    }

    void setNextFilter(AbstractFieldFilter nextFilter) {
        this.nextFilter = nextFilter;
    }

}
