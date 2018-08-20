package com.ymatou.dynamicsource;

import com.ymatou.dynamicsource.config.DaoMapperAdvice;
import com.ymatou.dynamicsource.regist.DbObject;
import com.ymatou.dynamicsource.util.DataSourceOperator;
import com.ymatou.dynamicsource.util.Pair;
import com.ymatou.dynamicsource.util.PropertiesLoader;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 动态数据源自动配置类
 */
@Configuration
@EnableConfigurationProperties(DynamicDsProperties.class)
@ConditionalOnProperty(prefix = "dynamic.ds", name = "enable", havingValue = "true")
public class DynamicDSAutoConfiguration {

    private final DataSourceOperator dataSourceOperator = DataSourceOperator.getInstance();

    @Autowired
    private DynamicDsProperties dsProperties;

    @Bean
    @ConditionalOnMissingBean(DataSource.class)
    public DataSource dataSource() {
        Map<String, DbObject> dbObjectMap = PropertiesLoader.loadDynamicFile();
        Pair<Map<String, DataSource>, Map<String, DataSource>> dataSourcePair = dataSourceOperator
                .getAndOperateDataSource(dbObjectMap, true);

        Map<String, DataSource> writeDataSource = dataSourcePair.getLeft();

        DataSource defaultDs = dataSourceOperator.updateAndGetDefaultDs(dataSourcePair,
                dsProperties.getDefaultWrite(), dsProperties.getDefaultRead());
        if (Objects.isNull(defaultDs)) {
            defaultDs = writeDataSource.values().stream().findFirst().get();
        }
        DynamicDataSource dynamicDataSource = new DynamicDataSource();
        dynamicDataSource.setDefaultTargetDataSource(defaultDs);

        writeDataSource.putAll(dataSourcePair.getRight());
        dynamicDataSource.setTargetDataSources(new HashMap<>(writeDataSource));
        return dynamicDataSource;
    }

    @Bean
    public AspectJExpressionPointcut pointcut() {
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression(dsProperties.getPointCut());
        return pointcut;
    }

    @Bean
    public DaoMapperAdvice advice() {
        return new DaoMapperAdvice();
    }

    @Bean
    public Advisor advisor() {
        return new DefaultPointcutAdvisor(pointcut(), advice());
    }

}
