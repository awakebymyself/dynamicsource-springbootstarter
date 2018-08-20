package com.ymatou.dynamicsource.config;

import com.ymatou.dynamicsource.annotation.DynamicDS;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;

import static com.ymatou.dynamicsource.config.Constants.READ_DATASOURCE_PREFIX;

public class DaoMapperAdvice implements MethodInterceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(DaoMapperAdvice.class);

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Method method = invocation.getMethod();
        DynamicDS dynamicDS = method.getAnnotation(DynamicDS.class);
        if (dynamicDS != null) {
            DataSourceContext.setDataSource(dynamicDS.value());
        } else {
            String methodName = method.getName();
            Optional<String> any = Arrays.stream(READ_DATASOURCE_PREFIX).filter(methodName::startsWith).findAny();
            if (any.isPresent()) {
                DataSourceContext.useReadDataSource();
            } else {
                DataSourceContext.useDefaultWriteDs();
            }
        }
        LOGGER.debug("Use DataSource : {}", DataSourceContext.getDataSource());
        try {
            return invocation.proceed();
        } finally {
            DataSourceContext.clear();
        }
    }

}
