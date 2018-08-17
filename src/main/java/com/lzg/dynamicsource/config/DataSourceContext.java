package com.lzg.dynamicsource.config;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 持有当前线程所拥有的数据源
 */
public class DataSourceContext {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataSourceContext.class);
    //写数据源对应的keys
    private static final List<String> writeDsKeys = new ArrayList<>();
    // 读数据源对应的keys
    private static final List<String> readDsKeys = new ArrayList<>();
    private static final AtomicLong counter = new AtomicLong(0);
    private static String DEFAULT_WRITE_DATA_SOURCE = "";
    private static final ThreadLocal<String> DATA_SOURCE = ThreadLocal.withInitial(()
            -> DEFAULT_WRITE_DATA_SOURCE);
    private static String DEFAULT_READ_DATA_SOURCE = "";

    public static void useDefaultWriteDs() {
        LOGGER.debug("Use default write DATA_SOURCE");
        if (StringUtils.isNotBlank(DEFAULT_WRITE_DATA_SOURCE)) {
            DATA_SOURCE.set(DEFAULT_WRITE_DATA_SOURCE);
        } else {
            DATA_SOURCE.set(writeDsKeys.stream().findFirst().get());
        }
    }

    public static void useReadDataSource() {
        LOGGER.debug("Use read data source!");

        if (StringUtils.isNotBlank(DEFAULT_READ_DATA_SOURCE)) {
            DATA_SOURCE.set(DEFAULT_READ_DATA_SOURCE);
        } else {
            int index = counter.intValue() % readDsKeys.size();
            String dataSourceKey;
            try {
                dataSourceKey = readDsKeys.get(index);
            } catch (RuntimeException e) {
                LOGGER.warn("Error occurs when switch read DATA_SOURCE, change to write");
                useDefaultWriteDs();
                return;
            }
            counter.incrementAndGet();
            DATA_SOURCE.set(dataSourceKey);
        }
    }

    public static void setReadDsKeys(Collection<String> keys) {
        if (keys == null || keys.isEmpty()) {
            LOGGER.warn("No read data source keys can be found!");
            return;
        }
        readDsKeys.addAll(keys);
    }

    public static void setWriteDsKeys(Collection<String> keys) {
        if (keys == null || keys.isEmpty()) {
            LOGGER.warn("No write data source keys can be found!");
            return;
        }
        writeDsKeys.addAll(keys);
    }

    public static void setDefaultWriteDataSource(String defaultWriteDataSource) {
        DEFAULT_WRITE_DATA_SOURCE = defaultWriteDataSource;
    }

    public static void setDefaultReadDataSource(String defaultReadDataSource) {
        DEFAULT_READ_DATA_SOURCE = defaultReadDataSource;
    }

    public static String getDataSource() {
        return DATA_SOURCE.get();
    }

    public static void setDataSource(String dataSource) {
        if (!writeDsKeys.contains(dataSource) && !readDsKeys.contains(dataSource)) {
            throw new IllegalStateException("DataSource key doesn't exist!");
        }
        DATA_SOURCE.set(dataSource);
    }

    public static void clear() {
        DATA_SOURCE.remove();
    }


}
