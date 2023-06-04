package issac.constant;

/**
 * ********************************************************************
 * ProjectName   ：  realtime
 * Package       ：  issac.constant
 * ClassName     ：  DimConstant
 * CreateTime    ：  2022-11-11 00:21
 * Author        ：  lihuashiyu
 * Email         ：  lihuashiyu@github.com
 * IDE           ：  IntelliJ IDEA 2020.3.4
 * Version       ：  1.0
 * CodedFormat   ：  utf-8
 * Description   ：  DimConstant 被用于 ==>
 * ********************************************************************
 */

public class DimConstant
{
    public static final String KAFKA_SOURCE_ID_NAME = "dim-kafka-data-source";      // kafka source 的 id 和 用户名
    public static final String MYSQL_SOURCE_ID_NAME = "dim-mysql-source";           // mysql-cdc 的 id 和 用户名
    public static final String PHOENIX_SINK_ID_NAME = "dim-mysql-source";           // mysql-cdc 的 id 和 用户名
    
    public static final String KAFKA_CONSUMER_GROUP_ID = "mock_dim";                // DIM 读取 Kafka 的消费者组 ID
    
    public static final String FLINK_CDC_TYPE = "type";                             // Flink CDC 数据类型：insert
    public static final String FLINK_CDC_INSERT = "insert";                         // Flink CDC 数据类型：insert
    public static final String FLINK_CDC_UPDATE = "update";                         // Flink CDC 数据类型：update
    public static final String FLINK_CDC_BOOTSTRAP_INSERT = "bootstrap-insert";     // Flink CDC 数据类型：bootstrap-insert
    
    public static final String DIM_APP_NAME = "dim-app";                             // Flink CDC 数据类型：update
    public static final String DIM_MAP_STATE_DESCRIPTOR_NAME = "dim-map-state";     // 配置流处理为广播流的名称
}
