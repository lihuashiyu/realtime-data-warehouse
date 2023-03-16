package issac.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import issac.bean.VisitorStats;
import issac.mapper.VisitorStatsMapper;
import issac.service.VisitorStatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Desc: 访客统计接口的实现类
 */
@Service
public class VisitorStatsServiceImpl extends ServiceImpl<VisitorStatsMapper, VisitorStats> implements VisitorStatsService
{
    @Autowired
    VisitorStatsMapper visitorStatsMapper;
    
    @Override
    public List<VisitorStats> getVisitorStatsByNewFlag(int date)
    {
        return visitorStatsMapper.selectVisitorStatsByNewFlag(date);
    }
    
    @Override
    public List<VisitorStats> getVisitorStatsByHr(int date)
    {
        return visitorStatsMapper.selectVisitorStatsByHr(date);
    }
}
