package issac.utils;

import com.alibaba.druid.pool.DruidDataSource;
import issac.constant.ApplicationConstant;

public class DruidDSUtil
{
    private static DruidDataSource druidDataSource = null;
    
    
    public static DruidDataSource createDataSource()
    {
        druidDataSource = new DruidDataSource();                               // 创建连接池
        druidDataSource.setDriverClassName(ApplicationConstant.PHOENIX_DRIVER);    // 设置驱动全类名
        druidDataSource.setUrl(ApplicationConstant.PHOENIX_SERVER);                // 设置连接 url
        
        druidDataSource.setInitialSize(5);                                     // 设置初始化连接池时池中连接的数量
        druidDataSource.setMaxActive(20);                                      // 设置同时活跃的最大连接数
        druidDataSource.setMinIdle(1);                                         // 设置空闲时的最小连接数，必须介于 0 和最大连接数之间，默认为 0
        druidDataSource.setMaxWait(-1);                                        // 设置没有空余连接时的等待时间，超时抛出异常，-1 表示一直等待
        
        druidDataSource.setValidationQuery("select 1");                        // 验证连接是否可用使用的 SQL 语句
        
        // 如果检测失败，则连接将被从池中去除， 注意，默认值为 true，如果没有设置 validationQuery，则报错
        druidDataSource.setTestWhileIdle(true);                                // 指明连接是否被空闲连接回收器（如果有）进行检验
        druidDataSource.setTestOnBorrow(false);                                // 借出连接时，是否测试，设置为 false，不测试，否则很影响性能
        druidDataSource.setTestOnReturn(false);                                // 归还连接时，是否测试
        druidDataSource.setTimeBetweenEvictionRunsMillis(30 * 1000L);          // 设置空闲连接回收器每隔 30s 运行一次
        druidDataSource.setMinEvictableIdleTimeMillis(30 * 60 * 1000L);        // 设置池中连接空闲 30min 被回收，默认值即为 30 min
        
        return druidDataSource;
    }
}
