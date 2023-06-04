package issac.serialize;

import com.alibaba.fastjson.JSON;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.flink.api.common.serialization.SerializationSchema;

/**
 * ***********************************************************************************************
 * ProjectName   ：  test
 * Package       ：  com.yuanwang.serialize
 * ClassName     ：  KafkaDeserializationSchema
 * PATH          ：  src/main/java/com/yuanwang/serialize
 * CreateTime    ：  2023-06-02 09:03:00
 * USER          ：  admin
 * IDE           ：  IntelliJ IDEA 2022.3.2
 * Version       ：  1.0
 * CodedFormat   ：  utf-8
 * Description   ：  KafkaDeserializationSchema 被用于 ==> Kafka 序列化器
 * ***********************************************************************************************
 */
@Slf4j
@NoArgsConstructor
public class CustomSerializationSchema<T> implements SerializationSchema<T> {
    
    @Override
    public void open(InitializationContext context) throws Exception {
        SerializationSchema.super.open(context);
    }
    
    
    @Override
    public byte[] serialize(T element) {
        if (ObjectUtils.isEmpty(element)) {
            log.warn("序列化对象为空：Element = {} ", element);
            return "".getBytes();
        }
        
        byte[] elementBytes;
        try {
            elementBytes = JSON.toJSONBytes(element);
        } catch (Exception e) {
            log.error("将元素转为 byte 数组失败：Element = {} ", element);
            return "".getBytes();
        }
        
        return elementBytes;
    }
}
