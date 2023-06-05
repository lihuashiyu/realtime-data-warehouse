package issac.serialize;

import com.ververica.cdc.debezium.DebeziumDeserializationSchema;
import io.debezium.data.Envelope;
import issac.bean.MysqlCdcBean;
import issac.constant.SignalConstant;
import issac.constant.UtilConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.util.Collector;
import org.apache.kafka.connect.data.Field;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.source.SourceRecord;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
public class CdcDeserializationSchema implements DebeziumDeserializationSchema<MysqlCdcBean>
{
    private static final String BEFORE = "before";
    private static final String AFTER = "after";
    private static final String CREATE = "c";
    private static final String READ = "r";
    private static final String UPDATE = "u";
    private static final String DELETE = "d";
    private static final String TRUNCATE = "t";
    
    
    @Override
    public void deserialize(SourceRecord sourceRecord, Collector<MysqlCdcBean> out) throws Exception
    {
        // 获取数据
        Struct valueStruct = (Struct) sourceRecord.value();
        
        // 获取操作类型 
        String operation = getOperation(Envelope.operationFor(sourceRecord));
        
        // Before
        Map<String, String> beforeMap = getBefore(valueStruct.getStruct(BEFORE));
        
        // After
        Map<String, String> afterMap = getAfter(valueStruct.getStruct(AFTER));
        
        String topic = sourceRecord.topic();
        Long timestamp = sourceRecord.timestamp();
        
        String[] fields = topic.split(SignalConstant.RE_DOT);
        String database = fields[1];
        String tableName = fields[2];
        
        // 封装数据
        MysqlCdcBean mysqlCdcBean = new MysqlCdcBean(database, tableName, operation, beforeMap, afterMap, timestamp);
        
        // 输出封装好的数据
        out.collect(mysqlCdcBean);
    }
    
    
    @Override
    public TypeInformation<MysqlCdcBean> getProducedType()
    {
        return Types.POJO(MysqlCdcBean.class);
    }
    
    
    /**
     * 对 FlinkCDC 读取的 Mysql 数据操作变化类型进行整理
     * 
     * @param operation      ： 操作类型
     * @return               ： Mysql 数据变化类型
     */
    private String getOperation(Envelope.Operation operation)
    {
        String type = operation.toString().toLowerCase();
        switch (type)
        {
            case CREATE:
                return UtilConstant.CDC_INSERT;
            case READ:
                return UtilConstant.CDC_SELECT;
            case UPDATE:
                return UtilConstant.CDC_UPDATE;
            case DELETE:
                return UtilConstant.CDC_DELETE;
            case TRUNCATE:
                return UtilConstant.CDC_TRUNCATE;
            default:
                log.error("Flink CDC 读取的操作类型错误：Operation = {} ", type);
                return SignalConstant.EMPTY;
        }
    }
    
    
    /**
     * 获取数据变化前的数据
     * 
     * @param before    ： 变化前的数据
     * @return          ： 格式化后的数据
     */
    private Map<String, String> getBefore(Struct before)
    {
        Map<String, String> map = new HashMap<>();
    
        // insert 数据，则 before 为 null
        if (ObjectUtils.isNotEmpty(before))
        {
            Schema schema = before.schema();
            List<Field> fieldList = schema.fields();
            
            for (Field field : fieldList)
            {
                Object fieldValue = before.get(field);
                map.put(field.name(), ObjectUtils.isEmpty(fieldValue) ? SignalConstant.EMPTY : fieldValue.toString());
            }
        }
        
        return map;
    }
    
    
    /**
     * 获取数据变化后的数据
     *
     * @param after     ： 变化前的数据
     * @return          ： 格式化后的数据
     */
    private Map<String, String> getAfter(Struct after)
    {
        Map<String, String> map = new HashMap<>();
        
        // delete 数据，则 after 为 null
        if (ObjectUtils.isNotEmpty(after))
        {
            Schema schema = after.schema();
            List<Field> fieldList = schema.fields();
            
            for (Field field : fieldList)
            {
                Object fieldValue = after.get(field);
                map.put(field.name(), ObjectUtils.isEmpty(fieldValue) ? SignalConstant.EMPTY : fieldValue.toString());
            }
        }
        
        return map;
    }
}
