package issac.utils;

import cn.hutool.core.util.StrUtil;
import issac.constant.ApplicationConstant;
import issac.constant.DwdConstant;
import issac.constant.SignalConstant;
import issac.constant.UtilConstant;
import issac.mapper.KafkaDDL;
import issac.serialize.CustomKafkaDeserializationSchema;
import issac.serialize.CustomKafkaSerializationSchema;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.connector.base.DeliveryGuarantee;
import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema;
import org.apache.flink.connector.kafka.sink.KafkaSink;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.KafkaSourceBuilder;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
import org.apache.flink.streaming.connectors.kafka.KafkaDeserializationSchema;
import org.apache.flink.streaming.connectors.kafka.KafkaSerializationSchema;
import org.apache.flink.streaming.connectors.kafka.partitioner.FlinkFixedPartitioner;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.OffsetResetStrategy;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;

import java.util.Collections;
import java.util.Properties;

@Slf4j
public class KafkaUtil
{
    
    /**
     * 订阅读取 Kafka 数据
     * 
     * @param groupId    ： Kafka 消费者组 ID
     * @param topics     ： Kafka 需要消费的 topics
     * @param <T>        ： 反序列化对象
     *
     * @return ： 反序列化的 Kafka Source
     */
    public static <T> KafkaSource<T> getKafkaSource(Class<T> clazz, String groupId, String... topics) 
    {
        ParameterTool properties = ConfigurationUtil.getProperties();
        return getKafkaSource(properties, clazz, groupId, topics);
    }
    
    
    /**
     * 订阅读取 Kafka 数据
     *
     * @param properties ： 配置参数
     * @param clazz      ： 反序列化的 类型
     * @param groupId    ： Kafka 消费者组 ID
     * @param topics     ： Kafka 需要消费的 topics
     * @param <T>        ： 反序列化对象
     *
     * @return ： 反序列化的 Kafka Source
     */
    public static <T> KafkaSource<T> getKafkaSource(ParameterTool properties, Class<T> clazz, String groupId, String... topics) 
    {
        String kafkaServer = properties.get(ApplicationConstant.KAFKA_SERVER);
        if (StringUtils.isBlank(kafkaServer)) 
        {
            log.error("连接 Kafka 的服务器错误：KafkaBootstrapServers = {} ", kafkaServer);
            throw new RuntimeException();
        }
        
        String offsetString = properties.get(ApplicationConstant.KAFKA_SOURCE_OFFSET).toLowerCase();
        OffsetsInitializer offsets;
        switch (offsetString) 
        {
            case UtilConstant.COMMIT_OFFSET:
                offsets = OffsetsInitializer.committedOffsets(OffsetResetStrategy.EARLIEST);
                break;
            case UtilConstant.EARLIEST_OFFSET:
                offsets = OffsetsInitializer.earliest();
                break;
            case UtilConstant.LATEST_OFFSET:
                offsets = OffsetsInitializer.latest();
                break;
            // 从时间戳大于等于指定时间戳（毫秒）的数据开始消费
            default:
                offsets = OffsetsInitializer.timestamp(Long.parseLong(offsetString));
                break;
        }
        
        if (ArrayUtils.isEmpty(topics)) 
        {
            log.error("输入的 Kafka 的 Topic 错误：KafkaTopic = {} ", topics);
            throw new RuntimeException();
        }
        
        return KafkaSource.<T>builder()
            .setBootstrapServers(kafkaServer)
            .setGroupId(groupId)
            .setTopics(topics)
            .setStartingOffsets(offsets)
            .setValueOnlyDeserializer(new CustomKafkaDeserializationSchema<>(clazz))
            .build();
    }
    
    
    /**
     * 写入 Kafka 数据
     *
     * @param topic      ： 写入的 Topic
     * @param <T>        ： 序列化对象
     *
     * @return ： 序列化的 Kafka Sink
     */
    public static <T> KafkaSink<T> getKafkaSink(String topic) 
    {
        return getKafkaSink(ConfigurationUtil.getProperties(), topic);
    }
    
    
    /**
     * 写入 Kafka 数据
     *
     * @param properties ： 配置参数
     * @param topic      ： 写入的 Topic                  
     * @param <T>        ： 序列化对象
     *
     * @return ： 序列化的 Kafka Sink
     */
    public static <T> KafkaSink<T> getKafkaSink(ParameterTool properties, String topic) 
    {
        String kafkaServer = properties.get(ApplicationConstant.KAFKA_SERVER);
        if (StringUtils.isBlank(kafkaServer)) 
        {
            log.error("连接 Kafka 的服务器错误：KafkaBootstrapServers = {} ", kafkaServer);
            throw new RuntimeException();
        }
        
        if (StringUtils.isBlank(topic)) 
        {
            log.error("写入 Kafka 的 Topic 错误：KafkaTopic = {} ", topic);
            throw new RuntimeException();
        }
        
        // 配置序列化参数
        KafkaRecordSerializationSchema<T> serializationSchema = KafkaRecordSerializationSchema.<T>builder()
            .setTopic(topic)
            .setKeySerializationSchema(new CustomKafkaSerializationSchema<>())
            .setValueSerializationSchema(new CustomKafkaSerializationSchema<>())
            .setPartitioner(new FlinkFixedPartitioner<>())
            .build();
        
        // 获取数据写入策略
        String delivery = properties.get(ApplicationConstant.KAFKA_SINK_DELIVERY).toLowerCase();
        DeliveryGuarantee guarantee;
        switch (delivery) 
        {
            case UtilConstant.AT_LEAST_ONCE:
                guarantee = DeliveryGuarantee.AT_LEAST_ONCE;
                break;
            case UtilConstant.EXACTLY_ONCE:
                guarantee = DeliveryGuarantee.EXACTLY_ONCE;
                break;
            default:
                guarantee = DeliveryGuarantee.NONE;
                break;
        }
        
        String timeout = properties.get(ApplicationConstant.KAFKA_SINK_TRANSACTION_TIMEOUT, UtilConstant.TRANSACTION_TIMEOUT_SIZE);
        return KafkaSink.<T>builder()
            .setProperty(UtilConstant.TRANSACTION_TIMEOUT, timeout)
            .setBootstrapServers(kafkaServer)
            .setRecordSerializer(serializationSchema)
            .setDeliveryGuarantee(guarantee)
            .build();
    }
    
    
    public static FlinkKafkaConsumer<String> getFlinkKafkaConsumer(String topic, String groupId)
    {
        Properties properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, ApplicationConstant.KAFKA_SERVER);
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        
        KafkaDeserializationSchema<String> kafkaDeserializationSchema = new KafkaDeserializationSchema<String>()
        {
            @Override
            public boolean isEndOfStream(String nextElement) { return false; }
            
            @Override
            public String deserialize(ConsumerRecord<byte[], byte[]> record)
            {
                if (ObjectUtils.isEmpty(record) || ObjectUtils.isEmpty(record.value())) 
                { 
                    return SignalConstant.SPACE; 
                } else 
                { 
                    return new String(record.value()); 
                }
            }
            
            @Override
            public TypeInformation<String> getProducedType() { return BasicTypeInfo.STRING_TYPE_INFO; }
        };
        
        return new FlinkKafkaConsumer<>(topic, kafkaDeserializationSchema, properties);
    }
    
    
    public static KafkaSource<String> getFlinkKafkaConsumer(String brokerServers, String topic, int partition, String groupId)
    {
        KafkaSourceBuilder<String> builder = KafkaSource.builder();
        KafkaSource<String> kafkaSource = builder.setBootstrapServers(brokerServers)
            .setTopics(topic)
            .setGroupId(groupId)
            .setStartingOffsets(OffsetsInitializer.earliest())
            .setValueOnlyDeserializer(new SimpleStringSchema())
            .setPartitions(Collections.singleton(new TopicPartition(topic, partition)))
            .build();
    
        log.info("kafka-source = {} ", kafkaSource);
        
        return kafkaSource;
    }
    
    
    public static FlinkKafkaProducer<String> getFlinkKafkaProducer(String topic)
    {
        return new FlinkKafkaProducer<>(ApplicationConstant.KAFKA_SERVER, topic, new SimpleStringSchema());
    }
    
    
    public static FlinkKafkaProducer<String> getFlinkKafkaProducer(String topic, String defaultTopic)
    {
        Properties properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, ApplicationConstant.KAFKA_SERVER);
        
        return new FlinkKafkaProducer<>(defaultTopic, (KafkaSerializationSchema<String>) (element, timestamp) ->
            {
                if (StringUtils.isEmpty(element))
                {
                    return new ProducerRecord<>(topic, SignalConstant.EMPTY.getBytes());
                }
                
                return new ProducerRecord<>(topic, element.getBytes());
            }, properties, FlinkKafkaProducer.Semantic.EXACTLY_ONCE);
    }
    
    
    /**
     * Kafka-Source DDL 语句
     *
     * @param topic   数据源主题
     * @param groupId 消费者组
     * @return 拼接好的 Kafka 数据源 DDL 语句
     */
    public static String getKafkaDDL(String topic, String groupId)
    {
        KafkaDDL ddl = new KafkaDDL();
        return ddl.getKafkaDDL(topic, ApplicationConstant.KAFKA_SERVER, groupId);
    }
    
    
    /**
     * Kafka-Sink DDL 语句
     *
     * @param topic   : 输出到 Kafka 的目标主题
     * @return        : 拼接好的 Kafka-Sink DDL 语句
     */
    public static String getKafkaSinkDDL(String topic)
    {
        KafkaDDL ddl = new KafkaDDL();
        return ddl.getKafkaSinkDDL(topic, ApplicationConstant.KAFKA_SERVER);
    }
    
    
    /**
     * UpsertKafka-Sink DDL 语句
     *
     * @param topic 输出到 Kafka 的目标主题
     * @return 拼接好的 UpsertKafka-Sink DDL 语句
     */
    public static String getUpsertKafkaDDL(String topic)
    {
        KafkaDDL ddl = new KafkaDDL();
        return ddl.getUpsertKafkaDDL(topic, ApplicationConstant.KAFKA_SERVER);
    }
    
    
    /**
     * topic_db主题的  Kafka-Source DDL 语句
     *
     * @param groupId 消费者组
     * @return 拼接好的 Kafka 数据源 DDL 语句
     */
    public static String getTopicDb(String groupId)
    {
        String kafkaDDL = getKafkaDDL(DwdConstant.DWD_BASE_DB_KAFKA_TOPIC, groupId);
        KafkaDDL ddl = new KafkaDDL();
        String topicDb = ddl.getTopicDb();
        return StrUtil.format("{} {}", topicDb, kafkaDDL);
    }
}
