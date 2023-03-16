package issac.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import issac.bean.KeywordStats;
import issac.mapper.KeywordStatsMapper;
import issac.service.KeywordStatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

// 关键词统计接口实现类
@Service
public class KeywordStatsServiceImpl extends ServiceImpl<KeywordStatsMapper, KeywordStats> implements KeywordStatsService
{
    @Autowired
    KeywordStatsMapper keywordStatsMapper;
    
    
    @Override
    public List<KeywordStats> getKeywordStats(int date, int limit)
    {
        return keywordStatsMapper.selectKeywordStats(date, limit);
    }
}
