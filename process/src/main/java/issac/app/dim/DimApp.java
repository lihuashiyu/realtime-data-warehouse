package issac.app.dim;

import com.alibaba.fastjson.JSONObject;
import com.ververica.cdc.connectors.mysql.source.MySqlSource;
import com.ververica.cdc.connectors.mysql.table.StartupOptions;
import com.ververica.cdc.debezium.JsonDebeziumDeserializationSchema;
import issac.app.func.DimSinkFunction;
import issac.app.func.KafkaFlatMapFunction;
import issac.app.func.TableProcessFunction;
import issac.bean.TableProcess;
import issac.constant.ConfigConstant;
import issac.constant.DimConstant;
import issac.constant.SignalConstant;
import issac.utils.ConfigurationUtil;
import issac.utils.KafkaUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.streaming.api.datastream.BroadcastConnectedStream;
import org.apache.flink.streaming.api.datastream.BroadcastStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;

import java.util.Map;

// 数据流：web/app -> nginx -> 业务服务器 -> Mysql(binlog) -> Maxwell -> Kafka(ODS) -> FlinkApp -> Phoenix
// 程  序：Mock -> Mysql(binlog) -> Maxwell -> Kafka(ZK) -> DimApp(FlinkCDC/Mysql) -> Phoenix(HBase/ZK/HDFS)
@Slf4j
public class DimApp
{
    public static void main(String[] args) throws Exception
    {
        // 0. 获取配置文件中的数据
        Map<String, String> flinkCdcMap = ConfigurationUtil.parseApplicationProperty();
        
        // 1.获取执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(Integer.parseInt(flinkCdcMap.get(ConfigConstant.FLINK_KAFKA_PARALLELISM)));                                                 // 生产环境中设置为 Kafka 主题的分区数
        
        // 1.1 开启 CheckPoint
        // env.enableCheckpointing(5 * 60000L, CheckpointingMode.EXACTLY_ONCE);
        // env.getCheckpointConfig().setCheckpointTimeout(Long.parseLong(flinkCdcMap.get(ConfigConstant.CHECK_POINT_TIMEOUT)));
        // env.getCheckpointConfig().setMaxConcurrentCheckpoints(Integer.parseInt(flinkCdcMap.get(ConfigConstant.CHECK_POINT_MAX_CONCURRENT)));
        // env.setRestartStrategy(RestartStrategies.fixedDelayRestart(flinkCdcMap.get(ConfigConstant.CHECK_POINT_FIXED_DELAY_RESTART) 3, 5000L));
        
        // 1.2 设置状态后端
        // env.setStateBackend(new HashMapStateBackend());
        // env.getCheckpointConfig().setCheckpointStorage(flinkCdcMap.get(ConfigConstant.CHECK_POINT_DIR));
        // System.setProperty(ConfigConstant.HDFS_USER_NAME, flinkCdcMap.get(ConfigConstant.CHECK_POINT_USER_NAME));
        
        // 2. 读取 Kafka topic_db 主题数据创建主流
        FlinkKafkaConsumer<String> consumer = KafkaUtil.getFlinkKafkaConsumer(DimConstant.KAFKA_DIM_TOPIC, DimConstant.KAFKA_DIM_CONSUMER_GROUP);
        
        DataStreamSource<String> kafkaDS = env.addSource(consumer);
        // env.fromSource(consumer, WatermarkStrategy.noWatermarks(), "kafka-dim-source");
        
        // 3. 过滤掉非 JSON 数据 & 保留新增、变化以及初始化数据并将数据转换为 JSON 格式
        SingleOutputStreamOperator<JSONObject> filterJsonObjDS = kafkaDS.flatMap(new KafkaFlatMapFunction());
        
        // 4. 使用 FlinkCDC 读取 MySQL 配置信息表创建配置流
        MySqlSource<String> mySqlSource = MySqlSource.<String>builder()
            .hostname(flinkCdcMap.get(ConfigConstant.MYSQL_HOST))
            .port(Integer.parseInt(flinkCdcMap.get(ConfigConstant.MYSQL_PORT)))
            .username(flinkCdcMap.get(ConfigConstant.MYSQL_USER))
            .password(flinkCdcMap.get(ConfigConstant.MYSQL_PASSWORD))
            .databaseList(flinkCdcMap.get(ConfigConstant.FLINK_MYSQL_DATABASE).split(SignalConstant.COMMA))
            .tableList(flinkCdcMap.get(ConfigConstant.FLINK_MYSQL_TABLE).split(SignalConstant.COMMA))
            .startupOptions(StartupOptions.initial())
            .deserializer(new JsonDebeziumDeserializationSchema())
            .build();
        
        // 配置水位线
        DataStreamSource<String> mysqlSourceDS = env.fromSource(mySqlSource, WatermarkStrategy.noWatermarks(), DimConstant.MYSQL_DATASOURCE_NAME);
        
        // 5. 将配置流处理为广播流
        MapStateDescriptor<String, TableProcess> mapStateDescriptor = new MapStateDescriptor<>(DimConstant.DIM_MAP_STATE_DESCRIPTOR_NAME, String.class, TableProcess.class);
        BroadcastStream<String> broadcastStream = mysqlSourceDS.broadcast(mapStateDescriptor);
        
        // 6. 连接主流与广播流
        BroadcastConnectedStream<JSONObject, String> connectedStream = filterJsonObjDS.connect(broadcastStream);
        
        // 7. 处理连接流，根据配置信息处理主流数据
        SingleOutputStreamOperator<JSONObject> dimDS = connectedStream.process(new TableProcessFunction(mapStateDescriptor));
        
        // 8. 将数据写出到 Phoenix
        // dimDS.print(">>>>>>>>>>>>");
        dimDS.addSink(new DimSinkFunction());
        
        // 9. 启动任务
        env.execute(DimConstant.DIM_APP_NAME);
    }
}
