package issac.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KeywordBean
{
    private String stt;                          // 窗口起始时间
    private String edt;                          // 窗口闭合时间
    // @TransientSink
    private String source;                       // 关键词来源   ---  辅助字段,不需要写入 ClickHouse 
    private String keyword;                      // 关键词
    private Long keyword_count;                  // 关键词出现频次
    private Long ts;                             // 时间戳
} 
