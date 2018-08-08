package com.lzg.dynamicsource.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 持有当前线程所拥有的数据源
 */
public class DataSourceContext {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataSourceContext.class);

    private static final String DEFAULT_DATA_SOURCE = "master";

    private static final ThreadLocal<String> dataSource = ThreadLocal.withInitial(() -> DEFAULT_DATA_SOURCE);

    private static AtomicReference<String> masterDataSource = new AtomicReference<>(DEFAULT_DATA_SOURCE);

    // 从数据源对应的keys
    private static final List<String> slaveDsKeys = new CopyOnWriteArrayList<>();

    private static final AtomicLong counter = new AtomicLong(0);

    public static void useMasterDataSource() {
        LOGGER.info("Use master dataSource");
        dataSource.set(masterDataSource.get());
    }


    public static void useSlaveDataSource() {
        LOGGER.info("Use slave data source!");

        int index = counter.intValue() % slaveDsKeys.size();
        String dataSourceKey;
        try {
            dataSourceKey = slaveDsKeys.get(index);
        } catch (RuntimeException e) {
            LOGGER.warn("Error occurs when switch slave dataSource, change to master");
            useMasterDataSource();
            return;
        }
        counter.incrementAndGet();
        dataSource.set(dataSourceKey);
    }

    public static void setMasterDataSource(String masterDs) {
        masterDataSource.updateAndGet(v -> masterDs);
    }

    public static void setSlaveDsKeys(Collection<String> keys) {
        slaveDsKeys.addAll(keys);
    }

    public static String getDataSource() {
        return dataSource.get();
    }

    public static void clear() {
        dataSource.remove();
    }


}
