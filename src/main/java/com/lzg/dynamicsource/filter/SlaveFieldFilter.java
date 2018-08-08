package com.lzg.dynamicsource.filter;

import com.lzg.dynamicsource.regist.DbObject;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * @author 刘志钢
 */
public class SlaveFieldFilter extends AbstractFieldFilter {

    private static final String SLAVE_PREFIX = "slave";

    public SlaveFieldFilter(Object o, Map<String, DbObject> dbObjectMap) {
        super(SLAVE_PREFIX, o, dbObjectMap);
    }

    @Override
    protected boolean canHandle(Field field) {
        if (field.getName().startsWith(SLAVE_PREFIX)) {
            return true;
        }
        return false;
    }

}
