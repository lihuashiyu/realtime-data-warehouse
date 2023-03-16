package issac.service;

import com.baomidou.mybatisplus.extension.service.IService;
import issac.bean.KeywordStats;

import java.util.List;

// 关键词统计接口
public interface KeywordStatsService extends IService<KeywordStats>
{
    List<KeywordStats> getKeywordStats(int date, int limit);
}
