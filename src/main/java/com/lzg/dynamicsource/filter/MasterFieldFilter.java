package com.lzg.dynamicsource.filter;

import com.lzg.dynamicsource.regist.DbObject;

import java.lang.reflect.Field;
import java.util.Map;

import static com.lzg.dynamicsource.config.Constants.MASTER_PREFIX;

/**
 * @author 刘志钢
 */
public class MasterFieldFilter extends AbstractFieldFilter {

    public MasterFieldFilter(Object o, Map<String, DbObject> dbObjectMap) {
        super(MASTER_PREFIX, o, dbObjectMap);
    }

    @Override
    public boolean canHandle(Field field) {
        if (field.getName().startsWith(prefix)) {
            return true;
        }
        return false;
    }

}
