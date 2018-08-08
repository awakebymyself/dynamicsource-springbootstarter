package com.lzg.dynamicsource;

import com.lzg.dynamicsource.config.DaoMapperAdvice;
import com.lzg.dynamicsource.regist.DbObject;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
