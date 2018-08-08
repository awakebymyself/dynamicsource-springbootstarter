package com.lzg.dynamicsource.regist;

import com.alibaba.druid.pool.DruidDataSource;
import com.lzg.dynamicsource.DynamicDataSource;
import com.lzg.dynamicsource.DynamicDsProperties;
import com.lzg.dynamicsource.config.DataSourceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

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

        Map<Object, Object> targetSources = new HashMap<>();
        dbObjectMap.forEach((key, value) -> targetSources.putIfAbsent(key, getDataSource(value)));
        Object masterDataSource = targetSources.remove("master");
        if (masterDataSource == null) {
            throw new IllegalStateException("Master DataSource must specific!");
        }

        DataSourceContext.setMasterDataSource("master");
        DataSourceContext.setSlaveDsKeys(targetSources.keySet().stream().map(String::valueOf)
                .collect(Collectors.toList()));

        GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
        beanDefinition.setBeanClass(DynamicDataSource.class);
        beanDefinition.setSynthetic(true);
        MutablePropertyValues mpv = beanDefinition.getPropertyValues();

        mpv.addPropertyValue("defaultTargetDataSource", Objects.requireNonNull(masterDataSource));
        mpv.addPropertyValue("targetDataSources", targetSources);
        beanDefinitionRegistry.registerBeanDefinition("dataSource", beanDefinition);
    }

    private DataSource getDataSource(DbObject dbObject) {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl(dbObject.getUrl());
        dataSource.setName(dbObject.getUser());
        dataSource.setDriverClassName(dbObject.getDriver());
        dataSource.setUsername(dbObject.getUser());
        dataSource.setPassword(dbObject.getPassword());

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
