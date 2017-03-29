package org.mybatis.spring;

import org.springframework.aop.AfterReturningAdvice;
import org.springframework.aop.MethodBeforeAdvice;

import java.lang.reflect.Method;

/**
 * Created by wangjie on 2017/3/1.
 * 切换数据库
 */
public class DataSourceExchange implements MethodBeforeAdvice,AfterReturningAdvice {

    @Override
    public void afterReturning(Object o, Method method, Object[] objects, Object o1)
            throws Throwable {
        DataSourceContextHolder.clearDataSource();
    }

    @Override
    public void before(Method method, Object[] objects, Object o)
            throws Throwable {
        if (method.isAnnotationPresent(DataSource.class))
        {
            DataSource datasource = method.getAnnotation(DataSource.class);
            if(null == datasource || (null != datasource && null == datasource.name()))
                DataSourceContextHolder.setDataSource(datasource.cszm);
            else {
                DataSourceContextHolder.setDataSource(datasource.name());
            }
        }
    }
}
