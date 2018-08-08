package com.lzg.dynamicsource.filter;

import com.lzg.dynamicsource.regist.DbObject;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * @author 刘志钢
 */
public class CustomFieldFilter extends AbstractFieldFilter {


    CustomFieldFilter(String prefix, Object o, Map<String, DbObject> dbObjectMap) {
        super(prefix, o, dbObjectMap);
    }

    @Override
    protected boolean canHandle(Field field) {
        return false;
    }


}
