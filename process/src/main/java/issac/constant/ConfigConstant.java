package issac.constant;

/**
 * ********************************************************************
 * ProjectName   ：  realtime
 * Package       ：  issac.constant
 * ClassName     ：  ConnectConstant
 * CreateTime    ：  2022-11-06 13:40
 * Author        ：  Issac_Al
 * Email         ：  IssacAl@qq.com
 * IDE           ：  IntelliJ IDEA 2020.3.4
 * Version       ：  1.0
 * CodedFormat   ：  utf-8
 * Description   ：  GmallConstant 被用于 ==>
 * ********************************************************************
 */

public class ConfigConstant
{
    // 配置文件名
    public static final String PROPERTY_CONFIG_FILE_NAME = "application.properties";    // 应用 properties 配置文件名
    public static final String YAML_CONFIG_FILE_NAME = "application.yml";                // 应用 yml 配置文件名
    
    // Flink 配置文件名
    public static final String CHECK_POINT_DIR = "flink.checkpoint.dir";               
    public static final String CHECK_POINT_TIMEOUT = "flink.checkpoint.timeout";               
    public static final String CHECK_POINT_MAX_CONCURRENT = "flink.checkpoint.maxconcurrent";               
    public static final String CHECK_POINT_FIXED_DELAY_RESTART = "flink.checkpoint.fixeddelayrestart";               
    
    public static final String HDFS_USER_NAME = "HADOOP_USER_NAME";               
    public static final String CHECK_POINT_USER_NAME = "flink.checkpoint.username";               
    public static final String HDFS_PASS_WORD = "HDFS_PASS_WORD";
    public static final String CHECK_POINT_PASS_WORD = "flink.checkpoint.password";
    
    public static final String FLINK_KAFKA_PARALLELISM = "flink.kafka.parallelism";
    
    // Kafka 连接参数
    public static final String KAFKA_SERVER = "kafka.server";                  // Mysql 主机名称
    
    // Mysql 连接参数
    public static final String MYSQL_CONNECTION_URL = "mysql.url";             // Mysql 数据库名
    public static final String MYSQL_HOST = "mysql.host";                      // Mysql 主机名称
    public static final String MYSQL_PORT = "mysql.port";                      // Mysql 连接端口号
    public static final String MYSQL_USER = "mysql.user";                      // Mysql 用户名
    public static final String MYSQL_PASSWORD = "mysql.password";              // Mysql 密码
    public static final String MYSQL_DATABASE = "mysql.database";              // Mysq 数据库名
    public static final String MYSQL_DRIVER = "mysql.driver";                  // Mysql 驱动
    
    // Mysql 连接参数
    public static final String REDIS_HOST = "redis.host";                      // Redis 主机名称
    public static final String REDIS_PORT = "redis.port";                      // Redis 连接端口号
    public static final String REDIS_USER = "redis.user";                      // Redis 用户名
    public static final String REDIS_PASSWORD = "redis.password";              // Redis 密码
    public static final String REDIS_DATABASE = "redis.database";              // Redis数据库名
    public static final String REDIS_TIME_OUT = "redis.time.out";
    public static final String REDIS_MAX_TOTAL = "redis.max.total";
    public static final String REDIS_MAX_IDLE = "redis.max.idle";
    public static final String REDIS_MAX_WAIT_MILLIS = "redis.max.wait.millis";
    public static final String REDIS_MIN_IDLE = "redis.min.idle";
    public static final String REDIS_BLOCK_WHEN_EXHAUSTED = "redis.block.when.exhausted";
    public static final String REDIS_TEST_ON_BORROW = "redis.test.on.borrow";
    
    // Phoenix 配置
    public static final String HBASE_SCHEMA = "hbase.schema";                  // Phoenix 库名
    public static final String PHOENIX_DRIVER = "phoenix.driver";              // Phoenix 驱动
    public static final String PHOENIX_SERVER = "phoenix.server";              // Phoenix 连接参数
    
    // ClickHouse 配置
    public static final String CLICKHOUSE_DRIVER = "clickhouse.driver";        // ClickHouse 驱动
    public static final String CLICKHOUSE_URL = "clickhouse.url";              // ClickHouse 连接 URL
    
    // DIM 层：FlinkCDC 同步 Mysql 的表名 
    public static final String FLINK_MYSQL_DATABASE = "flink.cdc.mysql.database";        // Flink 同步 Mysql 数据库名
    public static final String FLINK_MYSQL_TABLE = "flink.cdc.mysql.table";              // Flink 同步 Mysql 数据表名
    public static final String FLINK_MYSQL_MAX_ROWS = "flink.lookup.cache.max-rows";     // Flink 同步 Mysql 数据缓存最大行数
    public static final String FLINK_MYSQL_TTL = "flink.lookup.cache.ttl";               // Flink 同步 Mysql 数据 TTL
    
    // 内部配置
    public static final String CONFIG_DIRECTORY = "config";                    // 配置文件夹名称
    public static final String CONF_DIRECTORY = "conf";                        // 配置文件夹名称
    public static final String FILE_ENCODING = "UTF-8";                        // 配置文件编码
    
}
