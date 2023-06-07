package issac.utils;

import com.alibaba.fastjson.JSONObject;
import com.ververica.cdc.connectors.mysql.source.MySqlSource;
import com.ververica.cdc.connectors.mysql.table.StartupOptions;
import com.ververica.cdc.debezium.DebeziumDeserializationSchema;
import io.debezium.data.Envelope;
import issac.bean.MysqlCdcBean;
import issac.bean.TableProcess;
import issac.constant.ApplicationConstant;
import issac.constant.SignalConstant;
import issac.serialize.CdcDeserializationSchema;
import lombok.SneakyThrows;
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
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

@Slf4j
public class FlinkCdcTest
{
    @SneakyThrows
    @Test
    public void cdcTest()
    {
        // 0. 获取 properties 配置参数
        ParameterTool properties = ConfigurationUtil.getProperties();
        
        // 1. 获取执行环境，并配置 CheckPoint
        StreamExecutionEnvironment env = FlinkConfigUtil.checkPointConfig(properties);
        
        // 2.通过 FlinkCDC 构建 Source
        CdcDeserializationSchema<TableProcess> deserializer = new CdcDeserializationSchema(MysqlCdcBean.class, TableProcess.class);
        
        MySqlSource<MysqlCdcBean<TableProcess>> mysqlSource = MySqlSource.<MysqlCdcBean<TableProcess>>builder()
            .hostname(properties.get(ApplicationConstant.FLINK_CDC_MYSQL_HOST))
            .port(properties.getInt(ApplicationConstant.FLINK_CDC_MYSQL_PORT))
            .username(properties.get(ApplicationConstant.FLINK_CDC_MYSQL_USER))
            .password(properties.get(ApplicationConstant.FLINK_CDC_MYSQL_PASSWORD))
            .databaseList(properties.get(ApplicationConstant.FLINK_CDC_MYSQL_DATABASE).split(SignalConstant.COMMA))
            .tableList(properties.get(ApplicationConstant.FLINK_CDC_MYSQL_TABLE).split(SignalConstant.COMMA))
            .startupOptions(StartupOptions.initial())
            // .deserializer(new RowDataDebeziumDeserializeSchema())
            // .deserializer(new SeekBinlogToTimestampFilter())
            // .deserializer(new StringDebeziumDeserializationSchema())
            // .deserializer(new JsonDebeziumDeserializationSchema())
            .deserializer(deserializer)
            .build();
    
        // 3.打印数据
        DataStreamSource<MysqlCdcBean<TableProcess>> dataStreamSource = env.fromSource(mysqlSource, WatermarkStrategy.noWatermarks(), "Mysql Source");
    
        dataStreamSource.print("Flink CDC Test =====> ").setParallelism(1);
        
        // 4.启动任务
        env.execute("Flink-CDC");
    }
    
    
    public static class ACustomStringDeserializationSchema implements DebeziumDeserializationSchema<String>
    {
        @Override
        public void deserialize(SourceRecord sourceRecord, Collector<String> collector)
        {
            // 构建结果对象
            JSONObject result = new JSONObject();
    
            // 获取数据库名称 表名称
            String topic = sourceRecord.topic();
            log.warn("Topic = {} ", topic);
            
            Map<String, ?> map = sourceRecord.sourcePartition();
            log.warn("SourcePartition = {} ", map);
            
            Map<String, ?> offsetMap = sourceRecord.sourceOffset();
            log.warn("OffsetMap = {} ", offsetMap);
            
            Integer partition = sourceRecord.kafkaPartition();
            log.warn("Partition = {} ", partition);
            
            Object key = sourceRecord.key();
            log.warn("Key = {} ", key);
            
            Object values = sourceRecord.value();
            log.warn("Values = {} ", values);
            
            Schema schema1 = sourceRecord.keySchema();
            log.warn("KeySchema = {} ", schema1);
            
            Schema schema2 = sourceRecord.valueSchema();
            log.warn("ValueSchema = {} ", schema2);
            
            String[] fields = topic.split(SignalConstant.RE_DOT);
            String database = fields[1];
            String tableName = fields[2];
            
            // 获取数据
            Struct value = (Struct) values;
            
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
            if ("create".equals(type))
            {
                type = "insert";
            }
            
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
