package com.lzg.dynamicsource;

import com.lzg.dynamicsource.config.DaoMapperAdvice;
import com.lzg.dynamicsource.config.DataSourceContext;
import com.lzg.dynamicsource.regist.DbObject;
import com.lzg.dynamicsource.util.DataSourceOperator;
import com.lzg.dynamicsource.util.Pair;
import com.lzg.dynamicsource.util.PropertiesLoader;
import org.apache.commons.lang3.StringUtils;
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


        Pair<Map<String, DataSource>, Map<String, DataSource>> dataSourcePair = dataSourceOperator
                .getAndOperateDataSource(dbObjectMap, true);

        Map<String, DataSource> writeDataSource = dataSourcePair.getLeft();
        String defaultWrite = dsProperties.getDefaultWrite();

        DataSource defaultDataSource;
        if (StringUtils.isNotBlank(defaultWrite)) {
            defaultDataSource = writeDataSource.get(defaultWrite);
            if (defaultDataSource == null) {
                throw new IllegalStateException("默认写数据源: " + defaultWrite + " 不存在！");
            }

            DataSourceContext.setDefaultWriteDataSource(defaultWrite);
        } else {
            defaultDataSource = writeDataSource.values().stream().findFirst().get();
        }

        DynamicDataSource dynamicDataSource = new DynamicDataSource();
        dynamicDataSource.setDefaultTargetDataSource(defaultDataSource);

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
