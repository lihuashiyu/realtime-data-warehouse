package issac;

import com.alibaba.fastjson.JSONObject;
import com.ververica.cdc.connectors.mysql.source.MySqlSource;
import com.ververica.cdc.connectors.mysql.table.StartupOptions;
import com.ververica.cdc.debezium.DebeziumDeserializationSchema;
import io.debezium.data.Envelope;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;
import org.apache.kafka.connect.data.Field;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.source.SourceRecord;

import java.util.List;

public class FlinkCDCDataStreamByMyDeser
{
    public static void main(String[] args) throws Exception
    {
        // 1.获取执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
    
        // 2、Flink-CDC将读取binlog的位置信息以状态的方式保存在CheckPoint
        // 2.1 所以需要开启检查点，这里开启 checkpoint，每隔 5s 做一次
        // env.enableCheckpointing(5000L);
        
        // 2.2 指定checkpoint的语义
        // env.getCheckpointConfig().setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);
        
        // 2.3 设置任务关闭的时候保留最后一次 checkpoint 的数据
        // env.getCheckpointConfig().enableExternalizedCheckpoints(CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);
        
        // 2.4 指定从 Checkpoint 自动重启策略
        // env.setRestartStrategy(RestartStrategies.fixedDelayRestart(3,2000L));
        
        // 2.5 设置状态后端
        // env.setStateBackend(new FsStateBackend("hdfs://issac:9000/flink"));
        
        // 2.6 设置访问 HDFS 的用户名
        // System.setProperty("HADOOP_USER_NAME","atguigu");
        
        // 3.通过 FlinkCDC 构建 Source
        MySqlSource<String> mysqlSource = MySqlSource.<String>builder()
            .hostname("issac")
            .port(3306)
            .username("issac")
            .password("111111")
            .databaseList("at_gui_gu")
            // .tableList("gmall-210225-flink.z_user_info")
            .startupOptions(StartupOptions.initial())
            .deserializer(new CustomStringDeserializationSchema())
            .build();
    
        DataStreamSource<String> dataStreamSource = env.fromSource(mysqlSource, WatermarkStrategy.noWatermarks(), "Mysql Source");
        
        // 4.打印数据
        dataStreamSource.print();
        
        // 5.启动任务
        env.execute("Flink-CDC");
    }
    
    
    public static class CustomStringDeserializationSchema implements DebeziumDeserializationSchema<String>
    {
        // {
        //      "database":"",
        //      "tableName":"",
        //      "data":{"id":"1001","tm_name","atguigu"....},
        //      "before":{"id":"1001","tm_name","atguigu"....},
        //      "type":"update",
        //      "ts":141564651515
        // }
        @Override
        public void deserialize(SourceRecord sourceRecord, Collector<String> collector) throws Exception
        {
            // 构建结果对象
            JSONObject result = new JSONObject();
            
            // 获取数据库名称&表名称
            String topic = sourceRecord.topic();
            String[] fields = topic.split("\\.");
            String database = fields[1];
            String tableName = fields[2];
            
            // 获取数据
            Struct value = (Struct) sourceRecord.value();
            
            // After
            Struct after = value.getStruct("after");
            JSONObject data = new JSONObject();
            if (after != null)
            { 
                // delete 数据，则 after 为 null
                Schema schema = after.schema();
                List<Field> fieldList = schema.fields();
    
                for (Field field : fieldList)
                {
                    Object fieldValue = after.get(field);
                    data.put(field.name(), fieldValue);
                }
            }
            
            // Before
            Struct before = value.getStruct("before");
            JSONObject beforeData = new JSONObject();
            if (before != null)
            { 
                // delete 数据，则 after 为 null
                Schema schema = before.schema();
                List<Field> fieldList = schema.fields();
    
                for (Field field : fieldList)
                {
                    Object fieldValue = before.get(field);
                    beforeData.put(field.name(), fieldValue);
                }
            }
            
            // 获取操作类型 CREATE UPDATE DELETE
            Envelope.Operation operation = Envelope.operationFor(sourceRecord);
            String type = operation.toString().toLowerCase();
            if ("create".equals(type)) { type = "insert"; }
            
            // 封装数据
            result.put("database", database);
            result.put("tableName", tableName);
            result.put("data", data);
            result.put("before", beforeData);
            result.put("type", type);
            // result.put("ts", System.currentTimeMillis());
            
            // 输出封装好的数据
            collector.collect(result.toJSONString());
        }
        
        
        @Override
        public TypeInformation<String> getProducedType() { return BasicTypeInfo.STRING_TYPE_INFO; }
    }
}
