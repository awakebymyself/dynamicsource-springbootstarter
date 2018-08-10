package com.lzg.dynamicsource.regist;

import com.alibaba.druid.pool.DruidDataSource;
import com.lzg.dynamicsource.DynamicDataSource;
import com.lzg.dynamicsource.config.DataSourceContext;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static com.lzg.dynamicsource.config.Constants.*;
import static java.util.Objects.requireNonNull;

/**
 * @author 刘志钢
 */
public class DynamicDsRegister implements BeanDefinitionRegistryPostProcessor, ApplicationContextAware {

    private static final Logger LOGGER = LoggerFactory.getLogger(DynamicDsRegister.class);

    private final Class<?> dbClass;
    private ApplicationContext applicationContext;

    public DynamicDsRegister(Class<?> dbClass) {
        this.dbClass = dbClass;
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {
        Map<String, DbObject> dbObjectMap = DbClassParser.parse(dbClass, applicationContext);
        if (dbObjectMap.isEmpty()) {
            LOGGER.warn("No dataSource can be found!");
            return;
        }

        Map<String, DbObject> writeDataSource = new HashMap<>();
        Map<String, DbObject> readDataSource = new HashMap<>();
        dbObjectMap.forEach((dsKey, dsObject) -> {
            if (dsObject.isWrite()) {
                writeDataSource.put(dsKey, dsObject);
            } else {
                readDataSource.put(dsKey, dsObject);
            }
        });

        logDataSource(writeDataSource, readDataSource);
        setDataSourceContext(writeDataSource, readDataSource);

        Map<Object, Object> targetSources = new HashMap<>();
        dbObjectMap.forEach((key, value) -> targetSources.putIfAbsent(key, getDataSource(value)));

        GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
        beanDefinition.setBeanClass(DynamicDataSource.class);
        beanDefinition.setSynthetic(true);
        MutablePropertyValues mpv = beanDefinition.getPropertyValues();

        DataSource defaultDataSource = getDefaultDataSource(dbObjectMap, writeDataSource);
        mpv.addPropertyValue("defaultTargetDataSource", defaultDataSource);
        mpv.addPropertyValue("targetDataSources", targetSources);
        beanDefinitionRegistry.registerBeanDefinition("dataSource", beanDefinition);
    }

    private void logDataSource(Map<String, DbObject> writeDataSource, Map<String, DbObject> readDataSource) {
        LOGGER.info("*******************Write DataSource******************");
        writeDataSource.forEach((key,ds) -> {
            LOGGER.info("Write DataSource Name: {}, DataSource Value: {}", key, ds);
        });
        LOGGER.info("*******************Read DataSource*******************");
        readDataSource.forEach((key,ds) -> {
            LOGGER.info("Read DataSource Name: {}, DataSource Value: {}", key, ds);
        });
    }

    private void setDataSourceContext(Map<String, DbObject> writeDataSource, Map<String, DbObject> readDataSource) {
        DataSourceContext.setReadDsKeys(readDataSource.keySet().stream().map(String::valueOf)
                .collect(Collectors.toList()));
        DataSourceContext.setWriteDsKeys(writeDataSource.keySet().stream().map(String::valueOf)
                .collect(Collectors.toList()));
    }

    private DataSource getDefaultDataSource(Map<String, DbObject> dbObjectMap, Map<String, DbObject> writeDataSource) {
        DbObject defaultDs;
        defaultDs = dbObjectMap.remove(MASTER_PREFIX);
        if (defaultDs == null && MapUtils.isEmpty(writeDataSource)) {
            throw new IllegalStateException("Must specific a master dataSource or write dataSource");
        }
        //noinspection ConstantConditions
        defaultDs = writeDataSource.entrySet().stream().findFirst().get().getValue();

        return getDataSource(defaultDs);
    }

    private DataSource getDataSource(DbObject dbObject) {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl(requireNonNull(dbObject.getUrl()));
        dataSource.setName(requireNonNull(dbObject.getUser()));
        dataSource.setDriverClassName(requireNonNull(dbObject.getDriver()));
        dataSource.setUsername(requireNonNull(dbObject.getUser()));
        dataSource.setPassword(requireNonNull(dbObject.getPassword()));

        return dataSource;
    }


    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;

    }
}
