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

    private static String masterDataSource = DEFAULT_DATA_SOURCE;

    // 从数据源对应的keys
    private static final List<String> slaveDsKeys = new ArrayList<>();

    private static final AtomicLong counter = new AtomicLong(0);

    // 走主数据源
    public static void useMasterDataSource() {
        LOGGER.debug("Use master DATA_SOURCE");
        DATA_SOURCE.set(masterDataSource);
    }

    // 走从数据源
    public static void useSlaveDataSource() {
        LOGGER.info("Use slave data source!");

        int index = counter.intValue() % slaveDsKeys.size();
        String dataSourceKey;
        try {
            dataSourceKey = slaveDsKeys.get(index);
        } catch (RuntimeException e) {
            LOGGER.warn("Error occurs when switch slave DATA_SOURCE, change to master");
            useMasterDataSource();
            return;
        }
        counter.incrementAndGet();
        DATA_SOURCE.set(dataSourceKey);
    }

    public static void setMasterDataSource(String masterDs) {
        masterDataSource = masterDs;
    }

    public static void setSlaveDsKeys(Collection<String> keys) {
        if (keys == null || keys.isEmpty()) {
            LOGGER.warn("No slave data sources can be found!");
            return;
        }
        slaveDsKeys.addAll(keys);
    }

    public static void setDataSource(String dataSource) {
        DATA_SOURCE.set(dataSource);
    }

    public static String getDataSource() {
        return DATA_SOURCE.get();
    }

    public static void clear() {
        DATA_SOURCE.remove();
    }


}
