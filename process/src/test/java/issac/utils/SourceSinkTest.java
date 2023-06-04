package issac.utils;

import issac.bean.LoginEvent;
import issac.bean.LoginSource;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.RestOptions;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.streaming.api.datastream.DataStreamSink;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;

/**
 * ***********************************************************************************************
 * ProjectName   ：  test
 * Package       ：  test.utils
 * ClassName     ：  SourceSinkTest
 * PATH          ：  src/test/java/test/utils
 * CreateTime    ：  2023-06-01 17:16:00
 * USER          ：  admin
 * IDE           ：  IntelliJ IDEA 2022.3.2
 * Version       ：  1.0
 * CodedFormat   ：  utf-8
 * Description   ：  SourceSinkTest 被用于 ==>
 * ***********************************************************************************************
 */
@Slf4j
public class SourceSinkTest
{
    @SneakyThrows
    @Test
    public void kafkaSourceTest()
    {
        // 1. 配置 WEB-UI 
        Configuration configuration = new Configuration();
        configuration.setInteger(RestOptions.PORT, 9090);
        configuration.setBoolean(RestOptions.ENABLE_FLAMEGRAPH, true);
        
        // 2. 获取执行环境
        StreamExecutionEnvironment env = FlinkConfigUtil.getFlinkEnv(configuration);
        
        // 3. 配置水位线策略
        WatermarkStrategy<LoginEvent> watermarkStrategy = FlinkConfigUtil.watermarkStrategy();
        WatermarkStrategy<LoginEvent> strategy = watermarkStrategy.withIdleness(Duration.ofSeconds(10));
        
        // 4. 获取数据源
        KafkaSource<LoginEvent> kafkaSource = KafkaUtil.getKafkaSource(LoginEvent.class, "test", "mock-log");
        DataStreamSource<LoginEvent> dataSourceStream = env.fromSource(kafkaSource, strategy, "kafka-source");
        SingleOutputStreamOperator<LoginEvent> dataSource = dataSourceStream.uid("kafka-source").name("kafka-source");
        
        // 5. 打印到控制台 Kafka
        dataSource.print().uid("out-console").name("out-console");
        
        // 6. 执行
        log.warn("ExecutionPlan =============> \n{} \n", env.getExecutionPlan());
        env.execute("user-defined-source-flink-kafka-test ...... ");
    }
    
    
    @SneakyThrows
    @Test
    public void kafkaSinkTest()
    {
        // 1. 配置 WEB-UI 
        Configuration configuration = new Configuration();
        configuration.setInteger(RestOptions.PORT, 9091);
        configuration.setBoolean(RestOptions.ENABLE_FLAMEGRAPH, true);
        
        // 2. 获取执行环境
        StreamExecutionEnvironment env = FlinkConfigUtil.getFlinkEnv(configuration);
        
        // 3. 配置水位线策略
        WatermarkStrategy<LoginEvent> watermarkStrategy = FlinkConfigUtil.watermarkStrategy();
        WatermarkStrategy<LoginEvent> strategy = watermarkStrategy.withIdleness(Duration.ofSeconds(10));
        
        // 4. 获取数据源
        DataStreamSource<LoginEvent> dataSourceStream = env.addSource(new LoginSource());
        SingleOutputStreamOperator<LoginEvent> dataSource = dataSourceStream.uid("data-source").name("data-source");
        
        // 5. 添加水位线
        SingleOutputStreamOperator<LoginEvent> streamOperator = dataSource.assignTimestampsAndWatermarks(strategy);
        SingleOutputStreamOperator<LoginEvent> operator = streamOperator.uid("add-water-mark").name("add-water-mark");
        
        // 6. 写入 Kafka
        // operator.print();
        DataStreamSink<LoginEvent> outKafka = operator.sinkTo(KafkaUtil.getKafkaSink("mock-log"));
        outKafka.uid("out-kafka").name("out-kafka");
        
        // 7. 执行
        log.warn("ExecutionPlan =============> \n{} \n", env.getExecutionPlan());
        env.execute("user-defined-sink-flink-kafka-test ...... ");
    }
    
    
    @SneakyThrows
    @Test
    public void dorisSourceTest()
    {
        // 0. 配置 WEB-UI 
        Configuration configuration = new Configuration();
        configuration.setInteger(RestOptions.PORT, 9090);
        configuration.setBoolean(RestOptions.ENABLE_FLAMEGRAPH, true);
        
        // 1. 获取配置文件
        ParameterTool properties = ConfigurationUtil.getProperties();
        
        // 2. 获取执行环境
        StreamExecutionEnvironment env = FlinkConfigUtil.getFlinkEnv(configuration);
        env.setRestartStrategy(RestartStrategies.fallBackRestart());
        
        // 3. 获取数据源
        SingleOutputStreamOperator<List<?>> dataSource = DorisUtil.getDorisSource(env, properties);
        
        // 4. 打印
        dataSource.print().uid("out-console").name("out-console");
        
        // 6. 执行
        log.warn("ExecutionPlan =============> \n{} \n", env.getExecutionPlan());
        env.execute("user-defined-source-flink-doris-test ...... ");
    }
    
}
