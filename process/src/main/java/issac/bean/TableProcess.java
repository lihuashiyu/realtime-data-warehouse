package issac.bean;

import lombok.Data;

@Data
public class TableProcess
{
    String sourceTable;                          // 来源表
    String sinkTable;                            // 输出表
    String sinkColumns;                          // 输出字段
    String sinkPk;                               // 主键字段
    String sinkExtend;                           // 建表扩展
}
