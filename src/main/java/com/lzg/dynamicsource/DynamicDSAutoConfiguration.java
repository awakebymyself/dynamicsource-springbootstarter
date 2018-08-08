package com.lzg.dynamicsource;

import com.alibaba.druid.pool.DruidDataSource;
import com.lzg.dynamicsource.config.DaoMapperAdvice;
import com.lzg.dynamicsource.config.DataSourceContext;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Short for dynamic-data-source
 */
@Configuration
@EnableConfigurationProperties(DynamicDsProperties.class)
public class DynamicDSAutoConfiguration {

    @Autowired
    private DynamicDsProperties dsProperties;

   // @Bean
    public DataSource master() {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl(dsProperties.getMasterUrl());
        dataSource.setName(dsProperties.getMasterName());
        dataSource.setDriverClassName(dsProperties.getMasterJdbcDriver());
        dataSource.setUsername(dsProperties.getMasterUserName());
        dataSource.setPassword(dsProperties.getMasterPassword());
        return dataSource;
    }

  //  @Bean
    public DataSource slave() {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl(dsProperties.getSlaveUrl());
        dataSource.setName(dsProperties.getSlaveName());
        dataSource.setDriverClassName(dsProperties.getSlaveJdbcDriver());
        dataSource.setUsername(dsProperties.getSlaveUserName());
        dataSource.setPassword(dsProperties.getSlavePassword());
        return dataSource;
    }

    @Bean
    public DataSource dynamicDataSource() {
        DynamicDataSource dynamicDataSource = new DynamicDataSource();
        dynamicDataSource.setDefaultTargetDataSource(master());

        Map<Object,Object> targetDataSource = new HashMap<>(4);
        targetDataSource.put("master", master());
        targetDataSource.put("slave", slave());
        dynamicDataSource.setTargetDataSources(targetDataSource);

        DataSourceContext.setSlaveDsKeys(Collections.singletonList("slave"));
        DataSourceContext.setMasterDataSource("master");

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
