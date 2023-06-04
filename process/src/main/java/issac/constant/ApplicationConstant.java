package issac.constant;

/**
 * ********************************************************************
 * ProjectName   ：  realtime
 * Package       ：  issac.constant
 * ClassName     ：  ConnectConstant
 * CreateTime    ：  2022-11-06 13:40
 * Author        ：  lihuashiyu
 * Email         ：  lihuashiyu@github.com
 * IDE           ：  IntelliJ IDEA 2020.3.4
 * Version       ：  1.0
 * CodedFormat   ：  utf-8
 * Description   ：  GmallConstant 被用于 ==>
 * ********************************************************************
 */

public class ApplicationConstant
{
    // ================================================== 配置文件名 ==================================================
    public static final String PROPERTY_CONFIG_FILE_NAME = "application.properties";     // 应用 properties 配置文件名
    public static final String YAML_CONFIG_FILE_NAME = "application.yml";                // 应用 yml 配置文件名
    
    // ============================================= Flink CheckPoint 配置 ============================================
    public static final String FLINK_CHECK_POINT_ENABLE = "flink.checkpoint.enable";     // 是否开启 CheckPoint
    public static final String FLINK_CHECK_POINT_DIR = "flink.checkpoint.storage";       // CheckPoint 存储路径
    public static final String FLINK_CHECK_POINT_TIMEOUT = "flink.checkpoint.timeout";   // CheckPoint 超时时间，超时会被抛弃，单位毫秒
    public static final String FLINK_CHECK_POINT_TIME = "flink.checkpoint.time";         // CheckPoint 多久保存一次
    public static final String FLINK_CHECK_POINT_MODE = "flink.checkpoint.mode";         // CheckPoint 模式：默认精确一次，EXACTLY_ONCE
    public static final String FLINK_CHECK_POINT_CLEANUP = "flink.checkpoint.cleanup";   // 开启在 job 中止后仍然保留的
    public final static String FLINK_CHECK_POINT_MIN_PAUSE = "flink.checkpoint.min.pause.between";           // CheckPoint 两次之间的时间，单位毫秒
    public static final String FLINK_CHECK_POINT_MAX_CONCURRENT = "flink.checkpoint.max.concurrent";         // CheckPoint 同一时间允许 CP 的个数       
    public static final String FLINK_CHECK_POINT_DELAY_RESTART = "flink.checkpoint.fixed.delay.restart";     // Flink 在 ChickPoint 错误后的重启策略          
    
    public static final String HDFS_USER_NAME = "flink.checkpoint.hdfs.username";        // hdfs 用户名
    public static final String HDFS_PASS_WORD = "flink.checkpoint.hdfs.password";        // hdfs 密码
    
    public static final String FLINK_KAFKA_PARALLELISM = "flink.kafka.parallelism";
    
    // ================================================= DORIS 连接参数 ================================================
    public final static String DORIS_HOST = "doris.host";                      // Doris 主机名
    public final static String DORIS_PORT = "doris.port";                      // Doris 端口号
    public final static String DORIS_USER = "doris.user";                      // Doris 用户名
    public final static String DORIS_PASSWORD = "doris.password";              // Doris 密码
    public final static String DORIS_DB = "doris.db";                          // Doris 数据库名称
    public final static String DORIS_TABLE = "doris.table";                    // Doris 表名
    public final static String DORIS_DRIVER = "doris.driver";                  // Doris 驱动名称
    public final static String DORIS_URL = "doris.url";                        // Doris 连接 url
    public final static String DORIS_FE_PORT = "doris.fe.port";                // Doris fe 端口号
    public final static String DORIS_BATCH = "doris.batch.size";               // 一次从 BE 读取数据的最大行数，1024
    public final static String DORIS_INTERVAL = "doris.interval";              // Doris 时间间隔
    public final static String DORIS_RETRY = "doris.retry";                    // 向 Doris 发送请求的重试次数
    public final static String DORIS_CONNECT_TIMEOUT = "doris.connect.timeout";     // 向 Doris 发送请求的连接超时时间
    public final static String DORIS_READ_TIMEOUT = "doris.read.timeout";      // 向 Doris 发送请求的读取超时时间
    public final static String DORIS_QUERY_TIMEOUT = "doris.query.timeout";    // 查询 Doris 的超时时间，默认值为1小时，-1表示无超时限制
    
    // ================================================= Redis 连接参数 ================================================
    public final static String REDIS_HOST = "redis.host";                      // Redis 主机名
    public final static String REDIS_PORT = "redis.port";                      // Redis 端口号
    public final static String REDIS_USER = "redis.user";                      // Redis 用户名
    public final static String REDIS_PASSWORD = "redis.password";              // Redis 密码
    public final static String REDIS_DB = "redis.db";                          // Redis 数据库编号
    public final static String REDIS_DRIVER = "redis.max.pool.size";           // Redis 最大线程池大小
    public final static String REDIS_BATCH = "redis.batch";                    // Redis 每批次大小
    public static final String REDIS_TIME_OUT = "redis.timeout";               // Redis 超时时长
    public static final String REDIS_MAX_TOTAL = "redis.max.total";
    public static final String REDIS_MAX_IDLE = "redis.max.idle";
    public static final String REDIS_MAX_WAIT_MILLIS = "redis.max.wait.time";  // Redis 最大等待时间
    public static final String REDIS_MIN_IDLE = "redis.min.idle";
    public static final String REDIS_BLOCK_WHEN_EXHAUSTED = "redis.block.when.exhausted";
    public static final String REDIS_TEST_ON_BORROW = "redis.test.on.borrow";
    
    // ================================================= Kafka 连接参数 ================================================
    public final static String KAFKA_SERVER = "kafka.bootstrap.server";        // Kafka 连接服务器地址
    public final static String KAFKA_SOURCE_OFFSET = "kafka.consumer.offset";  // Kafka 消费者偏移量
    public final static String KAFKA_SINK_PARTITION = "kafka.producer.partitioner";                     // Kafka 生产者分区器策略
    public final static String KAFKA_SINK_DELIVERY = "kafka.producer.delivery.guarantee";               // Kafka 生产者提交器策略
    public final static String KAFKA_SINK_TRANSACTION_TIMEOUT = "kafka.producer.transaction.timeout";   // Kafka 事务超时
    
    // ================================================= Mysql 连接参数 ================================================
    public static final String MYSQL_CONNECTION_URL = "mysql.url";             // Mysql 数据库名
    public static final String MYSQL_HOST = "mysql.host";                      // Mysql 主机名称
    public static final String MYSQL_PORT = "mysql.port";                      // Mysql 连接端口号
    public static final String MYSQL_USER = "mysql.user";                      // Mysql 用户名
    public static final String MYSQL_PASSWORD = "mysql.password";              // Mysql 密码
    public static final String MYSQL_DATABASE = "mysql.database";              // Mysq 数据库名
    public static final String MYSQL_DRIVER = "mysql.driver";                  // Mysql 驱动
    
    // ================================================ Phoenix 连接参数 ===============================================
    public static final String HBASE_SCHEMA = "hbase.schema";                  // Phoenix 库名
    public static final String PHOENIX_DRIVER = "phoenix.driver";              // Phoenix 驱动
    public static final String PHOENIX_SERVER = "phoenix.server";              // Phoenix 连接参数
    
    // ============================================== ClickHouse 连接参数 ==============================================
    public static final String CLICKHOUSE_DRIVER = "clickhouse.driver";        // ClickHouse 驱动
    public static final String CLICKHOUSE_URL = "clickhouse.url";              // ClickHouse 连接 URL
    
    // ============================================== FlinkCDC 同步参数 ==============================================
    public static final String FLINK_CDC_MYSQL_HOST = "flink.cdc.mysql.host";            // Flink 同步 Mysql 主机地址
    public static final String FLINK_CDC_MYSQL_PORT = "flink.cdc.mysql.port";            // Flink 同步 Mysql 端口号
    public static final String FLINK_CDC_MYSQL_USER = "flink.cdc.mysql.user";            // Flink 同步 Mysql 用户名
    public static final String FLINK_CDC_MYSQL_PASSWORD = "flink.cdc.mysql.password";    // Flink 同步 Mysql 密码
    public static final String FLINK_CDC_MYSQL_DATABASE = "flink.cdc.mysql.database";    // Flink 同步 Mysql 数据库名
    public static final String FLINK_CDC_MYSQL_TABLE = "flink.cdc.mysql.table";          // Flink 同步 Mysql 数据库表名
    public static final String FLINK_CDC_MYSQL_MAX_ROWS = "flink.lookup.cache.max-rows"; // Flink 同步 Mysql 数据缓存最大行数
    public static final String FLINK_CDC_MYSQL_TTL = "flink.lookup.cache.ttl";           // Flink 同步 Mysql 数据 TTL
    
    // ============================================ 各层消费写入 Kafka 配置 ============================================
    public static final String DIM_SOURCE_KAFKA_TOPIC = "dim.source.kafka.topic";                    // DIM 读取 Kafka 的主题
    
    
}
