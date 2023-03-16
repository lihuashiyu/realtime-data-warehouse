package issac.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import issac.bean.ProvinceStats;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

// 按照地区统计交易额
@Mapper
public interface ProvinceStatsMapper extends BaseMapper<ProvinceStats>
{
    List<ProvinceStats> selectProvinceStats(@Param("date") int date);
}
