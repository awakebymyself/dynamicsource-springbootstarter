package com.lzg.dynamicsource.config;

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

    private static final String DEFAULT_DATA_SOURCE = "master";

    private static final ThreadLocal<String> DATA_SOURCE = ThreadLocal.withInitial(() -> DEFAULT_DATA_SOURCE);

    //写数据源对应的keys
    private static final List<String> writeDsKeys = new ArrayList<>();

    // 读数据源对应的keys
    private static final List<String> readDsKeys = new ArrayList<>();

    private static final AtomicLong counter = new AtomicLong(0);

    public static void useWriteDataSource() {
        LOGGER.debug("Use write DATA_SOURCE");
        DATA_SOURCE.set(writeDsKeys.get(0));
    }

    public static void useReadDataSource() {
        LOGGER.debug("Use read data source!");

        int index = counter.intValue() % readDsKeys.size();
        String dataSourceKey;
        try {
            dataSourceKey = readDsKeys.get(index);
        } catch (RuntimeException e) {
            LOGGER.warn("Error occurs when switch read DATA_SOURCE, change to write");
            useWriteDataSource();
            return;
        }
        counter.incrementAndGet();
        DATA_SOURCE.set(dataSourceKey);
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

    public static void setDataSource(String dataSource) {
        if (!writeDsKeys.contains(dataSource) && !readDsKeys.contains(dataSource)) {
            throw new IllegalStateException("DataSource key doesn't exist!");
        }
        DATA_SOURCE.set(dataSource);
    }

    public static String getDataSource() {
        return DATA_SOURCE.get();
    }

    public static void clear() {
        DATA_SOURCE.remove();
    }


}
