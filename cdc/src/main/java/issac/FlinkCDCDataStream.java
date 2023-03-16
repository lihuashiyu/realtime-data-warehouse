package issac;

import com.ververica.cdc.connectors.mysql.source.MySqlSource;
import com.ververica.cdc.connectors.mysql.table.StartupOptions;
import com.ververica.cdc.debezium.JsonDebeziumDeserializationSchema;
import com.ververica.cdc.debezium.StringDebeziumDeserializationSchema;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.runtime.state.filesystem.FsStateBackend;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

@Slf4j
public class FlinkCDCDataStream
{
    public static void main(String[] args) throws Exception
    {
        // 设置用户名
        // System.setProperty("HADOOP_USER_NAME", "atguigu");
        
        // 1.获取执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        log.error("开始了是。。。。。。。。。。。。。。。。。。。。。。");
        
        // 开启 CK
        // env.enableCheckpointing(5000L);
        // env.getCheckpointConfig().setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);
        // env.getCheckpointConfig().setCheckpointTimeout(10000L);
        // env.getCheckpointConfig().setMinPauseBetweenCheckpoints(1);
        // env.getCheckpointConfig().setMaxConcurrentCheckpoints(2);
        // env.setRestartStrategy();
        
        // memory   tm           jm
        // fs       tm           hdfs
        // rocksdb  本地磁盘      hdfs
        // env.setStateBackend(new FsStateBackend("hdfs://hadoop102:8020/flink/210225/ck"));
        // env.enableCheckpointing(3000);
        
        // 2.通过 FlinkCDC 构建 Source
        MySqlSource<String> mySqlSource = MySqlSource.<String>builder()
            .hostname("issac")
            .port(3306)
            .username("issac")
            .password("111111")
            .databaseList("at_gui_gu")
            .tableList("at_gui_gu.student")
            .startupOptions(StartupOptions.initial())
            .deserializer(new JsonDebeziumDeserializationSchema())
            .build();
        
        // 3.打印数据
        env.fromSource(mySqlSource, WatermarkStrategy.noWatermarks(), "MySQL Source")
            .setParallelism(1)                             // set 4 parallel source tasks
            .print().setParallelism(1);
        
        // 4.启动任务
        env.execute("Print MySQL Snapshot + Binlog");
    }
}
