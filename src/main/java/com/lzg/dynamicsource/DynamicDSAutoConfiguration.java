package com.lzg.dynamicsource;

import com.alibaba.druid.pool.DruidDataSource;
import com.lzg.dynamicsource.config.DaoMapperAdvice;
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

/**
 * Short for dynamic-data-source
 */
@Configuration
@EnableConfigurationProperties(DynamicDsProperties.class)
@ConditionalOnProperty(prefix = "dynamic.ds", name = "enable", havingValue = "true")
public class DynamicDSAutoConfiguration {

    @Autowired
    private DynamicDsProperties dsProperties;

    @Bean
    @ConditionalOnMissingBean(DataSource.class)
    public DataSource dataSource() {
        DynamicDataSource dynamicDataSource = new DynamicDataSource();
        dynamicDataSource.setDefaultTargetDataSource(defaultDataSource());

        return dynamicDataSource;
    }

    private DataSource defaultDataSource() {
        DruidDataSource druidDataSource = new DruidDataSource();

        return druidDataSource;
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
