package org.mybatis.spring;

import java.lang.annotation.*;

/**
 * Created by wangjie on 2017/3/1.
 */
@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataSource {
    String name() default DataSource.cszm;
    public static String cszm = "cszm";//出生证明数据库
    public static String fy = "fy";//妇幼数据库
}
