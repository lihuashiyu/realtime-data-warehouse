package issac.bean;

import issac.annotation.Attribute;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TableProcess
{
    @Attribute(value = "source_table")
    String sourceTable;                          // 来源表
    
    @Attribute(value = "sink_table")
    String sinkTable;                            // 输出表
    
    @Attribute(value = "sink_columns")
    String sinkColumns;                          // 输出字段
    
    @Attribute(value = "sink_pk")
    String sinkPk;                               // 主键字段
    
    @Attribute(value = "sink_extend")
    String sinkExtend;                           // 建表扩展
}

