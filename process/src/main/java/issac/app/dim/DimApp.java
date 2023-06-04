package issac.app.dim;

import com.alibaba.fastjson.JSONObject;
import com.ververica.cdc.connectors.mysql.source.MySqlSource;
import com.ververica.cdc.connectors.mysql.table.StartupOptions;
import com.ververica.cdc.debezium.JsonDebeziumDeserializationSchema;
import issac.app.func.DimSinkFunction;
import issac.app.func.KafkaFlatMapFunction;
import issac.app.func.TableProcessFunction;
import issac.bean.TableProcess;
import issac.constant.ApplicationConstant;
import issac.constant.DimConstant;
import issac.constant.SignalConstant;
import issac.utils.ConfigurationUtil;
import issac.utils.FlinkConfigUtil;
import issac.utils.KafkaUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.BroadcastConnectedStream;
import org.apache.flink.streaming.api.datastream.BroadcastStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;

// 数据流：web/app -> nginx -> 业务服务器 -> Mysql(binlog) -> Maxwell -> Kafka(ODS) -> FlinkApp -> Phoenix
// 程  序：Mock -> Mysql(binlog) -> Maxwell -> Kafka(ZK) -> DimApp(FlinkCDC/Mysql) -> Phoenix(HBase/ZK/HDFS)
@Slf4j
public class DimApp
{
    public static void main(String[] args) throws Exception
    {
        // 1. 获取配置文件
        ParameterTool properties = ConfigurationUtil.getProperties();
        
        // 2.获取执行环境，并配置 CheckPoint 和 hdfs 代理
        StreamExecutionEnvironment env = FlinkConfigUtil.checkPointConfig(properties);
        FlinkConfigUtil.hadoopProxy(properties);
        
        // 3. 读取 Kafka topic_db 主题数据创建主流
        String topic = properties.get(ApplicationConstant.DIM_SOURCE_KAFKA_TOPIC);
        FlinkKafkaConsumer<String> consumer = KafkaUtil.getFlinkKafkaConsumer(topic, DimConstant.KAFKA_CONSUMER_GROUP_ID);
        SingleOutputStreamOperator<String> kafkaDS = env.addSource(consumer).uid(DimConstant.KAFKA_SOURCE_ID_NAME).name(DimConstant.KAFKA_SOURCE_ID_NAME);
        // env.fromSource(consumer, WatermarkStrategy.noWatermarks(), "kafka-dim-source");
        
        // 4. 过滤掉非 JSON 数据 & 保留新增、变化以及初始化数据并将数据转换为 JSON 格式
        SingleOutputStreamOperator<JSONObject> filterJsonObjDS = kafkaDS.flatMap(new KafkaFlatMapFunction());
        
        // 5. 使用 FlinkCDC 读取 MySQL 配置信息表创建配置流
        MySqlSource<String> mySqlSource = MySqlSource.<String>builder()
            .hostname(properties.get(ApplicationConstant.FLINK_CDC_MYSQL_HOST))
            .port(properties.getInt(ApplicationConstant.FLINK_CDC_MYSQL_PORT))
            .username(properties.get(ApplicationConstant.FLINK_CDC_MYSQL_USER))
            .password(properties.get(ApplicationConstant.FLINK_CDC_MYSQL_PASSWORD))
            .databaseList(properties.get(ApplicationConstant.FLINK_CDC_MYSQL_DATABASE).split(SignalConstant.COMMA))
            .tableList(properties.get(ApplicationConstant.FLINK_CDC_MYSQL_TABLE).split(SignalConstant.COMMA))
            .startupOptions(StartupOptions.initial())
            .deserializer(new JsonDebeziumDeserializationSchema())
            .build();
        
        // 6. 添加 Flink-cdc 源并配置水位线
        DataStreamSource<String> mysqlSourceDataStream = env.fromSource(mySqlSource, WatermarkStrategy.noWatermarks(), DimConstant.MYSQL_SOURCE_ID_NAME);
        SingleOutputStreamOperator<String> mysqlSourceDS = mysqlSourceDataStream.uid(DimConstant.MYSQL_SOURCE_ID_NAME).name(DimConstant.MYSQL_SOURCE_ID_NAME);
        
        // 7. 将配置流处理为广播流
        MapStateDescriptor<String, TableProcess> mapStateDescriptor = new MapStateDescriptor<>(DimConstant.DIM_MAP_STATE_DESCRIPTOR_NAME, String.class, TableProcess.class);
        BroadcastStream<String> broadcastStream = mysqlSourceDS.broadcast(mapStateDescriptor);
        
        // 8. 连接主流与广播流
        BroadcastConnectedStream<JSONObject, String> connectedStream = filterJsonObjDS.connect(broadcastStream);
        
        // 9. 处理连接流，根据配置信息处理主流数据
        SingleOutputStreamOperator<JSONObject> dimDS = connectedStream.process(new TableProcessFunction(mapStateDescriptor));
            
        // 10. 将数据写出到 Phoenix
        // dimDS.print(">>>>>>>>>>>>");
        dimDS.addSink(new DimSinkFunction()).uid(DimConstant.PHOENIX_SINK_ID_NAME).name(DimConstant.PHOENIX_SINK_ID_NAME);
        
        // 11. 启动任务
        env.execute(DimConstant.DIM_APP_NAME);
    }
}
