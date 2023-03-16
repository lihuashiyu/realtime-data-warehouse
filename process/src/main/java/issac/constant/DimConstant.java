package issac.constant;

/**
 * ********************************************************************
 * ProjectName   ：  realtime
 * Package       ：  issac.constant
 * ClassName     ：  DimConstant
 * CreateTime    ：  2022-11-11 00:21
 * Author        ：  Issac_Al
 * Email         ：  IssacAl@qq.com
 * IDE           ：  IntelliJ IDEA 2020.3.4
 * Version       ：  1.0
 * CodedFormat   ：  utf-8
 * Description   ：  DimConstant 被用于 ==>
 * ********************************************************************
 */

public class DimConstant
{
    public static final String KAFKA_DIM_TOPIC = "mock_db";                    // DIM 读取 Kafka 的主题
    public static final String KAFKA_DIM_CONSUMER_GROUP = "mock_dim";          // DIM 读取 Kafka 的消费者组
    
    public static final String MYSQL_DATASOURCE_NAME = "MysqlSource";          // DIM 读取 Kafka 的消费者组
    
    public static final String FLINK_CDC_TYPE = "type";                        // Flink CDC 数据类型：insert
    public static final String FLINK_CDC_INSERT = "insert";                    // Flink CDC 数据类型：insert
    public static final String FLINK_CDC_UPDATE = "update";                    // Flink CDC 数据类型：update
    public static final String FLINK_CDC_BOOTSTRAP_INSERT = "bootstrap-insert";          // Flink CDC 数据类型：bootstrap-insert
    
    public static final String DIM_APP_NAME = "DimApp";                        // Flink CDC 数据类型：update
    public static final String DIM_MAP_STATE_DESCRIPTOR_NAME = "MapState";     // 配置流处理为广播流的名称
    
}
