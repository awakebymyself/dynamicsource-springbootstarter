package com.lzg.dynamicsource.config;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

public class DaoMapperAdvice implements MethodInterceptor {


    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        String methodName = invocation.getMethod().getName();
        if (methodName.startsWith("select")) {
            DataSourceContext.useSlaveDataSource();
        } else {
            DataSourceContext.useMasterDataSource();
        }
        System.out.println("进入切面, 当前线程的数据源为：" + DataSourceContext.getDataSource());
        try {
            return invocation.proceed();
        } finally {
            DataSourceContext.clear();
        }
    }

}
