package issac.service;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.IService;
import issac.bean.ProvinceStats;

import java.util.List;

// 按照地区统计的业务接口
public interface ProvinceStatsService extends IService<ProvinceStats>
{
    List<ProvinceStats> getProvinceStats(int date);
}
