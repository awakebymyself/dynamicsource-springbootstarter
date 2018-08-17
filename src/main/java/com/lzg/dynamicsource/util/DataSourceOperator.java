package com.lzg.dynamicsource.util;

import com.alibaba.druid.pool.DruidDataSource;
import com.lzg.dynamicsource.config.DataSourceContext;
import com.lzg.dynamicsource.regist.DbObject;
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
        writeDataSource.forEach((key, ds) -> LOGGER.info("Write DataSource Name: {}, DataSource Value: {}", key, ds));
        LOGGER.info(">>>>>>>>>>>>>>>>>>>>>>>>Read DataSource<<<<<<<<<<<<<<<<<<<<<<<");
        readDataSource.forEach((key, ds) -> LOGGER.info("Read DataSource Name: {}, DataSource Value: {}", key, ds));
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
