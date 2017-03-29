package org.mybatis.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by wangjie on 2017/3/1.
 */
public class DataSourceContextHolder {
    /**
     * 日志对象
     */
    protected static Logger logger = LoggerFactory.getLogger(DataSourceContextHolder.class);

    private static final ThreadLocal<String> contextHolder = new ThreadLocal<String>();
    /**
     * @Description: 设置数据源类型
     * @param dataSource  数据源名称
     * @return void
     * @throws
     */
    public static void setDataSource(String dataSource) {
        logger.debug("--------------当前数据源:"+dataSource+"------------------");
        contextHolder.set(dataSource);
    }

    /**
     * @Description: 获取数据源名称
     * @param
     * @return String
     * @throws
     */
    public static String getDataSource() {
        return contextHolder.get();
    }

    /**
     * @Description: 清除数据源名称
     * @param
     * @return void
     * @throws
     */
    public static void clearDataSource() {
        contextHolder.remove();
    }
}
