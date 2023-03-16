package issac.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import issac.bean.ProvinceStats;
import issac.mapper.ProvinceStatsMapper;
import issac.service.ProvinceStatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Desc:按照地区统计的业务接口实现类
 */
@Service
public class ProvinceStatsServiceImpl extends ServiceImpl<ProvinceStatsMapper, ProvinceStats> implements ProvinceStatsService
{
    // 注入mapper
    @Autowired
    ProvinceStatsMapper provinceStatsMapper;
    
    @Override
    public List<ProvinceStats> getProvinceStats(int date)
    {
        return provinceStatsMapper.selectProvinceStats(date);
    }
}
