package com.ymatou.dynamicsource.regist;

import com.ymatou.dynamicsource.DynamicDataSource;
import com.ymatou.dynamicsource.util.DataSourceOperator;
import com.ymatou.dynamicsource.util.Pair;
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
import java.util.Objects;

/**
 * @author 刘志钢
 */
public class DynamicDsRegister implements BeanDefinitionRegistryPostProcessor, ApplicationContextAware {

    private static final Logger LOGGER = LoggerFactory.getLogger(DynamicDsRegister.class);

    private final Class<?> dbClass;
    private final DataSourceOperator dataSourceOperator = DataSourceOperator.getInstance();
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

        Pair<Map<String, DataSource>, Map<String, DataSource>> dataSourcePair = dataSourceOperator
                .getAndOperateDataSource(dbObjectMap, true);
        Map<String, DataSource> writeDataSource = dataSourcePair.getLeft();
        Map<String, DataSource> readDataSource = dataSourcePair.getRight();

        GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
        beanDefinition.setBeanClass(DynamicDataSource.class);
        beanDefinition.setSynthetic(true);

        MutablePropertyValues mpv = beanDefinition.getPropertyValues();

        DataSource defaultDataSource = dataSourceOperator.updateAndGetDefaultDs(dbClass, dataSourcePair);
        if (Objects.isNull(defaultDataSource)) {
            // 没有指定的默认数据源则使用第一个
            defaultDataSource = writeDataSource.values().stream().findFirst().get();
        }
        mpv.addPropertyValue("defaultTargetDataSource", defaultDataSource);

        // 将读写数据源合并
        writeDataSource.putAll(readDataSource);
        mpv.addPropertyValue("targetDataSources", new HashMap<>(writeDataSource));

        beanDefinitionRegistry.registerBeanDefinition("dataSource", beanDefinition);
    }


    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;

    }
}
