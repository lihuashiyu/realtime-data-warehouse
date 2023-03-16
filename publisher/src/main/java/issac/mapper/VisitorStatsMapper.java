package issac.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import issac.bean.VisitorStats;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

// 访客统计接口
@Mapper
public interface VisitorStatsMapper extends BaseMapper<VisitorStats>
{
    List<VisitorStats> selectVisitorStatsByNewFlag(@Param("date") int date);
    
    // ClickHouse 的函数：if(条件表达式，满足返回的值，不满足返回的值) toHour 将日期转换为小时 （0~23）
    List<VisitorStats> selectVisitorStatsByHr(@Param("date") int date);
}
