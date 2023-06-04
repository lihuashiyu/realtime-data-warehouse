package issac.serialize;

import com.ververica.cdc.debezium.DebeziumDeserializationSchema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.util.Collector;
import org.apache.kafka.connect.source.SourceRecord;

/**
 * ********************************************************************
 * ProjectName   ：  realtime-data-warehouse
 * Package       ：  issac.serialize
 * ClassName     ：  CdcDeserializationSchema
 * CreateTime    ：  2023-06-04 23:03
 * Author        ：  Issac_Al
 * Email         ：  IssacAl@qq.com
 * IDE           ：  IntelliJ IDEA 2020.3.4
 * Version       ：  1.0
 * CodedFormat   ：  utf-8
 * Description   ：  CdcDeserializationSchema 被用于 ==>
 * ********************************************************************
 */
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CdcDeserializationSchema<T> implements DebeziumDeserializationSchema<T>
{
    private Class<T> t;
    
    
    @Override
    public void deserialize(SourceRecord record, Collector<T> out) throws Exception
    {
        
    }
    
    @Override
    public TypeInformation<T> getProducedType()
    {
        return TypeInformation.of(t);
    }
}
