package com.lzg.dynamicsource;

import com.alibaba.druid.pool.DruidDataSource;
import com.lzg.dynamicsource.config.DaoMapperAdvice;
import com.lzg.dynamicsource.regist.DbObject;
import com.lzg.dynamicsource.util.DataSourceOperator;
import com.lzg.dynamicsource.util.Pair;
import com.lzg.dynamicsource.util.PropertiesLoader;
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
import java.util.Map;

/**
 * Short for dynamic-data-source
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

        Pair<Map<String, DbObject>, Map<String, DbObject>> dataSourcePair = dataSourceOperator
                .getAndOperateDataSource(dbObjectMap, true);

        DynamicDataSource dynamicDataSource = new DynamicDataSource();
        dynamicDataSource.setDefaultTargetDataSource(defaultDataSource());

        return dynamicDataSource;
    }

    private DataSource defaultDataSource() {
        DruidDataSource dataSource = new DruidDataSource();

        dataSource.setUrl("");
        dataSource.setUsername("");
        dataSource.setPassword("");
        dataSource.setDriverClassName("");
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
