package com.lzg.dynamicsource.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 指定动态数据源
 *
 * @author 刘志钢
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface DynamicDS {

    /**
     * 指定数据源的名称
     *
     * @return value
     */
    String value();

}
