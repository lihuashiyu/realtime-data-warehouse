package issac.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import issac.bean.KeywordStats;
import issac.bean.ProductStats;
import issac.mapper.KeywordStatsMapper;
import issac.mapper.ProductStatsMapper;
import issac.service.KeywordStatsService;
import issac.service.ProductStatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

// 商品统计 Service 接口实现类
@Service
public class ProductStatsServiceImpl extends ServiceImpl<ProductStatsMapper, ProductStats> implements ProductStatsService
{
    // 自动注入   在容器中，寻找 ProductStatsMapper 类型的对象，赋值给当前属性
    @Autowired
    ProductStatsMapper productStatsMapper;

    @Override
    public BigDecimal getGMV(int date)
    {
        return productStatsMapper.getGMV(date);
    }

    @Override
    public List<ProductStats> getProductStatsByTrademark(int date, int limit)
    {
        return productStatsMapper.getProductStatsByTrademark(date, limit);
    }

    @Override
    public List<ProductStats> getProductStatsByCategory3(int date, int limit)
    {
        return productStatsMapper.getProductStatsByCategory3(date, limit);
    }

    @Override
    public List<ProductStats> getProductStatsBySPU(int date, int limit)
    {
        return productStatsMapper.getProductStatsBySPU(date, limit);
    }
}
