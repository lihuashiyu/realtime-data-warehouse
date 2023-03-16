package issac.utils;

import cn.hutool.core.util.StrUtil;
import issac.constant.ConfigConstant;
import issac.constant.DwdConstant;
import issac.constant.SignalConstant;
import issac.mapper.KafkaDDL;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
import org.apache.flink.streaming.connectors.kafka.KafkaDeserializationSchema;
import org.apache.flink.streaming.connectors.kafka.KafkaSerializationSchema;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;

public class IssacKafkaUtil
{
    public static FlinkKafkaConsumer<String> getFlinkKafkaConsumer(String topic, String groupId)
    {
        Properties properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, ConfigConstant.KAFKA_SERVER);
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
    
        KafkaDeserializationSchema<String> kafkaDeserializationSchema = new KafkaDeserializationSchema<String>()
        {
            @Override
            public boolean isEndOfStream(String nextElement) { return false; }
        
            @Override
            public String deserialize(ConsumerRecord<byte[], byte[]> record)
            {
                if (record == null || record.value() == null) { return SignalConstant.SPACE; } 
                else { return new String(record.value()); }
            }
        
            @Override
            public TypeInformation<String> getProducedType() { return BasicTypeInfo.STRING_TYPE_INFO; }
        };
    
        return new FlinkKafkaConsumer<>(topic, kafkaDeserializationSchema, properties);
    }
    
    
    public static FlinkKafkaProducer<String> getFlinkKafkaProducer(String topic)
    {
        return new FlinkKafkaProducer<>(ConfigConstant.KAFKA_SERVER, topic, new SimpleStringSchema());
    }
    
    
    public static FlinkKafkaProducer<String> getFlinkKafkaProducer(String topic, String defaultTopic)
    {
        Properties properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, ConfigConstant.KAFKA_SERVER);
        
        return new FlinkKafkaProducer<>(defaultTopic, (KafkaSerializationSchema<String>) (element, timestamp) ->
            {
                if (StringUtils.isEmpty(element))
                {
                    return new ProducerRecord<>(topic, "".getBytes());
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
        return ddl.getKafkaDDL(topic, ConfigConstant.KAFKA_SERVER, groupId);
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
        return ddl.getKafkaSinkDDL(topic, ConfigConstant.KAFKA_SERVER);
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
        return ddl.getUpsertKafkaDDL(topic, ConfigConstant.KAFKA_SERVER);
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
