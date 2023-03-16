package issac.service;

import com.baomidou.mybatisplus.extension.service.IService;
import issac.bean.ProductStats;
import issac.bean.ProvinceStats;
import issac.mapper.ProductStatsMapper;

import java.math.BigDecimal;
import java.util.List;

// 商品统计 Service 接口
public interface ProductStatsService extends IService<ProductStats>
{
    // 获取某一天交易总额
    BigDecimal getGMV(int date);
    
    // 获取某一天不同品牌的交易额
    List<ProductStats> getProductStatsByTrademark(int date, int limit);
    
    // 获取某一天不同品类的交易额
    List<ProductStats> getProductStatsByCategory3(int date, int limit);
    
    // 获取某一天不同SPU的交易额
    List<ProductStats> getProductStatsBySPU(int date, int limit);
}
