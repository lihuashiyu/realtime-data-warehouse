package issac.serialize;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONPathException;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.util.Collector;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

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
 * Description   ：  KafkaDeserializationSchema 被用于 ==> Kafka 反序列化器
 * ***********************************************************************************************
 */
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
public class CustomDeserializationSchema<T> implements DeserializationSchema<T>
{
    
    private Class<T> t;
    
    
    @Override
    public void open(InitializationContext context) throws Exception
    {
        DeserializationSchema.super.open(context);
    }
    
    
    @Override
    public T deserialize(byte[] message) throws IOException
    {
        if (ArrayUtils.isEmpty(message))
        {
            log.warn("日志信息为空：ConsumerRecord = {} ", message);
            return null;
        }
        
        T object;
        try
        {
            String value = new String(message, StandardCharsets.UTF_8);
            object = JSON.parseObject(value, t);
        } catch (JSONPathException e)
        {
            log.error("Error = {} ", e.getMessage(), e);
            log.error("将元素转为 byte 数组失败：Message = {} ", message);
            return null;
        }
        
        return object;
    }
    
    
    @Override
    public void deserialize(byte[] message, Collector<T> out) throws IOException
    {
        DeserializationSchema.super.deserialize(message, out);
    }
    
    
    @Override
    public boolean isEndOfStream(T nextElement)
    {
        return false;
    }
    
    
    @Override
    public TypeInformation<T> getProducedType()
    {
        return TypeInformation.of(t);
    }
}
