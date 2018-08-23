package com.lzg.dynamicsource.util;

import com.alibaba.druid.pool.DruidDataSource;
import com.lzg.dynamicsource.annotation.DefaultDataSource;
import com.lzg.dynamicsource.regist.DbObject;
import com.lzg.dynamicsource.config.DataSourceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

public class DataSourceOperator {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataSourceOperator.class);

    private DataSourceOperator() {
    }

    public static DataSourceOperator getInstance() {
        return InstanceHolder.INSTANCE;
    }

    public Pair<Map<String, DataSource>, Map<String, DataSource>> getAndOperateDataSource(
            Map<String, DbObject> dbObjectMap, boolean log) {
        Map<String, DataSource> writeDataSource = new HashMap<>();
        Map<String, DataSource> readDataSource = new HashMap<>();

        dbObjectMap.forEach((dsKey, dsObject) -> {
            if (dsObject.isWrite()) {
                writeDataSource.put(dsKey, getDataSource(dsObject));
            } else {
                readDataSource.put(dsKey, getDataSource(dsObject));
            }
        });
        if (log) {
            logDataSource(writeDataSource, readDataSource);
        }

        setDataSourceContext(writeDataSource, readDataSource);

        return Pair.of(writeDataSource, readDataSource);
    }

    /**
     * 获取默认数据源
     *
     * @param dbClass        disconf托管的配置类
     * @param dataSourcePair 保存的读写数据源的pair
     * @return 默认数据源
     */
    public DataSource updateAndGetDefaultDs(Class<?> dbClass,
                                            Pair<Map<String, DataSource>, Map<String, DataSource>> dataSourcePair) {
        if (dbClass.isAnnotationPresent(DefaultDataSource.class)) {
            DefaultDataSource annotation = dbClass.getAnnotation(DefaultDataSource.class);
            String writeDs = annotation.write();
            String readDs = annotation.read();
            return updateAndGetDefaultDs(dataSourcePair, writeDs, readDs);
        }
        return null;
    }

    public DataSource updateAndGetDefaultDs(Pair<Map<String, DataSource>, Map<String, DataSource>> dataSourcePair,
                                            String writeDs, String readDs) {
        DataSource writeDataSource = dataSourcePair.getLeft().get(writeDs);
        DataSource readDataSource = dataSourcePair.getRight().get(readDs);
        if (writeDataSource == null) {
            throw new IllegalStateException("Default write 数据源不存在！");
        }
        if (readDataSource == null) {
            throw new IllegalStateException("Default read 数据源不存在！");
        }
        DataSourceContext.setDefaultWriteDataSource(writeDs);
        DataSourceContext.setDefaultReadDataSource(readDs);
        return writeDataSource;
    }

    private DataSource getDataSource(DbObject dbObject) {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl(requireNonNull(dbObject.getUrl()));
        dataSource.setName(requireNonNull(dbObject.getUser()));
        dataSource.setDriverClassName(requireNonNull(dbObject.getDriver()));
        dataSource.setUsername(requireNonNull(dbObject.getUser()));
        dataSource.setPassword(requireNonNull(dbObject.getPassword()));

        //configuration
        dataSource.setInitialSize(20);
        dataSource.setMinIdle(10);
        dataSource.setMaxActive(500);
        dataSource.setTimeBetweenEvictionRunsMillis(60000);
        dataSource.setMinEvictableIdleTimeMillis(300000);
        dataSource.setValidationQuery("select 'X'");
        dataSource.setTestWhileIdle(true);
        dataSource.setTestOnBorrow(false);
        return dataSource;
    }

    private void logDataSource(Map<String, DataSource> writeDataSource, Map<String, DataSource> readDataSource) {
        LOGGER.info(">>>>>>>>>>>>>>>>>>>>>>>>Write DataSource<<<<<<<<<<<<<<<<<<<<<<<");
        writeDataSource.forEach((key, ds) -> LOGGER.info("Write DataSource Name: {}, DataSource Value: {}", key,
                ((DruidDataSource)ds).getUrl()));
        LOGGER.info(">>>>>>>>>>>>>>>>>>>>>>>>Read DataSource<<<<<<<<<<<<<<<<<<<<<<<");
        readDataSource.forEach((key, ds) -> LOGGER.info("Read DataSource Name: {}, DataSource Value: {}", key,
                ((DruidDataSource)ds).getUrl()));
    }

    private void setDataSourceContext(Map<String, DataSource> writeDataSource,
                                      Map<String, DataSource> readDataSource) {
        DataSourceContext.setReadDsKeys(readDataSource.keySet().stream().map(String::valueOf)
                .collect(Collectors.toList()));
        DataSourceContext.setWriteDsKeys(writeDataSource.keySet().stream().map(String::valueOf)
                .collect(Collectors.toList()));
    }

    private static final class InstanceHolder {

        private static final DataSourceOperator INSTANCE = new DataSourceOperator();
    }

}
