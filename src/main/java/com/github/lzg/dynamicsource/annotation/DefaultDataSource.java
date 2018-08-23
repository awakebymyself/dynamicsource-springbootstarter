package com.github.lzg.dynamicsource.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface DefaultDataSource {

    /**
     * 指定默认的写数据源
     *
     * @return write
     */
    String write();

    /**
     * 指定默认的读数据源
     *
     * @return read
     */
    String read();

}
