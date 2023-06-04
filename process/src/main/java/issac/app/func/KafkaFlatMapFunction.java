package issac.app.func;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import issac.constant.DimConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.util.Collector;

/**
 * ********************************************************************
 * ProjectName   ：  realtime-data-warehouse
 * Package       ：  issac.app.func
 * ClassName     ：  KafkKaFlatMapFunction
 * CreateTime    ：  2023-05-26 00:00
 * Author        ：  Issac_Al
 * Email         ：  IssacAl@qq.com
 * IDE           ：  IntelliJ IDEA 2020.3.4
 * Version       ：  1.0
 * CodedFormat   ：  utf-8
 * Description   ：  KafkKaFlatMapFunction 被用于 ==>
 * ********************************************************************
 */
@Slf4j
public class KafkaFlatMapFunction implements FlatMapFunction<String, JSONObject>
{
    @Override
    public void flatMap(String value, Collector<JSONObject> out) throws Exception
    {
        try
        {
            // 将数据转换为 JSON 格式
            JSONObject jsonObject = JSON.parseObject(value);
        
            // 获取数据中的操作类型字段
            String type = jsonObject.getString(DimConstant.FLINK_CDC_TYPE);
        
            // 保留新增、变化以及初始化数据
            if (DimConstant.FLINK_CDC_INSERT.equals(type) || DimConstant.FLINK_CDC_UPDATE.equals(type) || DimConstant.FLINK_CDC_BOOTSTRAP_INSERT.equals(type))
            {
                out.collect(jsonObject);
            }
        } catch (Exception e) 
        {
            log.warn("发现脏数据：{}", value); 
        }
    }
}
