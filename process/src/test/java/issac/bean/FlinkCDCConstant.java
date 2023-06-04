package issac.bean;

/**
 * **************************************************************************************************
 * ProjectName   ：  realtime
 * Package       ：  issac
 * ClassName     ：  FlinkCDCConstant
 * CreateTime    ：  2023-05-21 12:31
 * Author        ：  lihuashiyu
 * Email         ：  lihuashiyu@github.com
 * IDE           ：  IntelliJ IDEA 2020.3.4
 * Version       ：  1.0
 * CodedFormat   ：  utf-8
 * Description   ：  Java Class
 * **************************************************************************************************
 */

public class FlinkCDCConstant
{
    public static final String APPLICATION = "application.properties";         // 默认配置文件名称
    
    // Mysql 相关配置
    public static final String MYSQL_CONNECTION_URL = "mysql.url";             // Mysql 数据库名
    public static final String MYSQL_HOST = "mysql.host";                      // Mysql 主机名称
    public static final String MYSQL_PORT = "mysql.port";                      // Mysql 连接端口号
    public static final String MYSQL_USER = "mysql.user";                      // Mysql 用户名
    public static final String MYSQL_PASSWORD = "mysql.password";              // Mysql 密码
    public static final String MYSQL_DATABASE = "mysql.database";              // Mysq 数据库名
    public static final String MYSQL_DRIVER = "mysql.driver";                  // Mysql 驱动
    
    // FlinkCDC 同步 Mysql 的表名 
    public static final String CDC_MYSQL_DATABASES = "cdc.databases";          // Flink 同步 Mysql 数据库名
    public static final String CDC_MYSQL_TABLES = "cdc.tables";                // Flink 同步 Mysql 数据表名
    public static final String CDC_MYSQL_MAX_ROWS = "cdc.cache.max-rows";      // Flink 同步 Mysql 数据缓存最大行数
    public static final String CDC_MYSQL_TTL = "cdc.cache.ttl";                // Flink 同步 Mysql 数据 TTL
    
    // 数据库和表名切割符
    public static final String SPLIT_SYMBOL_COMMA = ",";
    public static final String SPLIT_SYMBO_DOT = "\\.";
}
