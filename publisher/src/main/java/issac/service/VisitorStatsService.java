package issac.service;

import com.baomidou.mybatisplus.extension.service.IService;
import issac.bean.VisitorStats;

import java.util.List;

// 访客统计业务层接口
public interface VisitorStatsService extends IService<VisitorStats>
{
    List<VisitorStats> getVisitorStatsByNewFlag(int date);
    
    List<VisitorStats> getVisitorStatsByHr(int date);
}
