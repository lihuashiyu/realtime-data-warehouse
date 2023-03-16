package issac.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import issac.bean.KeywordStats;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

// 关键词统计 Mapper
@Mapper
public interface KeywordStatsMapper extends BaseMapper<KeywordStats>
{
    List<KeywordStats> selectKeywordStats(@Param("date") int date, @Param("limit") int limit);
}
