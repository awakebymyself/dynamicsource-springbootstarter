package com.lzg.dynamicsource.filter;

import com.lzg.dynamicsource.regist.DbObject;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 刘志钢
 */
public class FieldFilterChain {

    public static Map<String, DbObject> parseFields(Field[] fields, Object object) {
        Map<String, DbObject> resultMap = new HashMap<>();

        AbstractFieldFilter fieldFilter = assemble(object, resultMap, fields);
        for (Field field : fields) {
            fieldFilter.filter(field);
        }
        return resultMap;
    }

    private static AbstractFieldFilter assemble(Object object, Map<String, DbObject> dbObjectMap, Field[] fields) {
        MasterFieldFilter masterFieldFilter = new MasterFieldFilter(object, dbObjectMap);
        SlaveFieldFilter slaveFieldFilter = new SlaveFieldFilter(object, dbObjectMap);
        masterFieldFilter.setNextFilter(slaveFieldFilter);
        slaveFieldFilter.setNextFilter(new CustomFieldFilter(object, fields, dbObjectMap));
        return masterFieldFilter;
    }

}
