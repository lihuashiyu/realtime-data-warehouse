package issac.bean;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Desc: 关键词统计实体类
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "keyword_stats", keepGlobalPrefix = true)
public class KeywordStats
{
    @TableField(value = "stt", keepGlobalFormat = true)
    private String stt;
    
    @TableField(value = "edt", keepGlobalFormat = true)
    private String edt;
    
    @TableField(value = "keyword", keepGlobalFormat = true)
    private String keyword;
    
    @TableField(value = "ct", keepGlobalFormat = true)
    private Long ct;
    
    @TableField(value = "ts", keepGlobalFormat = true)
    private String ts;
}

