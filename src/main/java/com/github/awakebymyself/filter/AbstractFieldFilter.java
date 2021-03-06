package com.github.awakebymyself.filter;

import com.github.awakebymyself.regist.DbObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static com.github.awakebymyself.config.Constants.DRIVER_SUFFIX;
import static com.github.awakebymyself.config.Constants.MASTER_PREFIX;
import static com.github.awakebymyself.config.Constants.PASS_SUFFIX;
import static com.github.awakebymyself.config.Constants.URL_SUFFIX;
import static com.github.awakebymyself.config.Constants.USER_SUFFIX;
import static com.github.awakebymyself.config.Constants.WRITE_DATASOURCE;

/**
 * @author 刘志钢
 */
public abstract class AbstractFieldFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractFieldFilter.class);
    protected final Map<String, DbObject> dbObjectMap;
    final String prefix;
    private final Object object;
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
                LOGGER.error("Error occurs when set field value", e);
            }
        }
        return Optional.empty();
    }

    protected abstract boolean canHandle(Field field);

    protected void doFilter(Field field) {
        dbObjectMap.putIfAbsent(prefix, new DbObject());
        DbObject dbObject = dbObjectMap.get(prefix);
        String writeOrRead = null;
        if (Objects.equals(MASTER_PREFIX, prefix)) {
            writeOrRead = WRITE_DATASOURCE;
        }
        setFiledValue(field, dbObject, writeOrRead);
    }

    void setFiledValue(Field field, DbObject dbObject, String writeOrRead) {
        getFieldValuePerSuffix(field, DRIVER_SUFFIX).ifPresent(dbObject::setDriver);
        getFieldValuePerSuffix(field, PASS_SUFFIX).ifPresent(dbObject::setPassword);
        getFieldValuePerSuffix(field, USER_SUFFIX).ifPresent(dbObject::setUser);
        getFieldValuePerSuffix(field, URL_SUFFIX).ifPresent(dbObject::setUrl);
        dbObject.setWrite(Objects.equals(WRITE_DATASOURCE, writeOrRead));
    }

}
