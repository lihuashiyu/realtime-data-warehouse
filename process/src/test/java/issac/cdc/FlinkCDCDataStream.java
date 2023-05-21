package issac.cdc;

import com.alibaba.fastjson.JSONObject;
import com.ververica.cdc.connectors.mysql.source.MySqlSource;
import com.ververica.cdc.connectors.mysql.table.StartupOptions;
import com.ververica.cdc.debezium.DebeziumDeserializationSchema;
import io.debezium.data.Envelope;
import issac.bean.FlinkCDCConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;
import org.apache.kafka.connect.data.Field;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.source.SourceRecord;

import java.util.List;

@Slf4j
public class FlinkCDCDataStream
{
    public static void main(String[] args) throws Exception
    {
        // 从参数中获取配置文件的路径，若未传入参数，则默认使用当前路径的 application.properties
        ParameterTool parameter;
        if (args.length == 0)
        {
            String path = FlinkCDCDataStream.class.getProtectionDomain().getCodeSource().getLocation().getPath();
            parameter = ParameterTool.fromPropertiesFile(path + FlinkCDCConstant.APPLICATION);
        } else if (args.length == 1)
        {
            parameter = ParameterTool.fromPropertiesFile(args[0]);
        } else
        {
            log.error("没有在 jar 路径下找到配置文件，也没有传入配置文件路径");
            throw new RuntimeException();
        }
        
        // 1.获取执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        
        // 开启 CK
        // env.enableCheckpointing(5000L);
        // env.getCheckpointConfig().setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);
        // env.getCheckpointConfig().setCheckpointTimeout(10000L);
        // env.getCheckpointConfig().setMinPauseBetweenCheckpoints(1);
        // env.getCheckpointConfig().setMaxConcurrentCheckpoints(2);
        // env.setRestartStrategy();
        // env.setStateBackend(new FsStateBackend("hdfs://master:9000/flink/check-point"));
        
        // 2.通过 FlinkCDC 构建 Source
        MySqlSource<String> mysqlSource = MySqlSource.<String>builder()
            .hostname(parameter.get(FlinkCDCConstant.MYSQL_HOST))
            .port(parameter.getInt(FlinkCDCConstant.MYSQL_PORT))
            .username(parameter.get(FlinkCDCConstant.MYSQL_USER))
            .password(parameter.get(FlinkCDCConstant.MYSQL_PASSWORD))
            .databaseList(parameter.get(FlinkCDCConstant.CDC_MYSQL_DATABASES).split(FlinkCDCConstant.SPLIT_SYMBOL_COMMA))
            .tableList(parameter.get(FlinkCDCConstant.CDC_MYSQL_TABLES).split(FlinkCDCConstant.SPLIT_SYMBOL_COMMA))
            .startupOptions(StartupOptions.initial())
            // .deserializer(new JsonDebeziumDeserializationSchema())
            .deserializer(new CustomStringDeserializationSchema())
            .build();
        
        // 3.打印数据
        DataStreamSource<String> dataStreamSource = env.fromSource(mysqlSource, WatermarkStrategy.noWatermarks(), "Mysql Source");
        dataStreamSource.setParallelism(1).print();
        
        // 4.启动任务
        env.execute("Flink-CDC");
    }
    
    
    public static class CustomStringDeserializationSchema implements DebeziumDeserializationSchema<String>
    {
        @Override
        public void deserialize(SourceRecord sourceRecord, Collector<String> collector)
        {
            // 构建结果对象
            JSONObject result = new JSONObject();
            
            // 获取数据库名称 表名称
            String topic = sourceRecord.topic();
            String[] fields = topic.split(FlinkCDCConstant.SPLIT_SYMBO_DOT);
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
