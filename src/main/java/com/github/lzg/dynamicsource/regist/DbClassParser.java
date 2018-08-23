package com.github.lzg.dynamicsource.regist;

import com.github.lzg.dynamicsource.filter.FieldFilterChain;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * @author 刘志钢
 */
public class DbClassParser {

    public static Map<String, DbObject> parse(Class<?> dbClass, ApplicationContext context) {
        Object bean = context.getBean(dbClass);
        Class<?> clazz = bean.getClass();
        Field[] fields = clazz.getDeclaredFields();
        return FieldFilterChain.parseFields(fields, bean);
    }

}
