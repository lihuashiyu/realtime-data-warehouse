package issac.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import issac.bean.KeywordStats;
import issac.bean.ProductStats;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.util.List;

// 商品主题统计的 Mapper 接口
@Mapper
public interface ProductStatsMapper extends BaseMapper<ProductStats>
{
    // 获取某一天商品的交易额
    BigDecimal getGMV(@Param("date") int date);
    
    // 获取某一天不同品牌的交易额：如果 mybatis 的方法中，有多个参数，每个参数前需要用@Param注解指定参数的名称*/
    List<ProductStats> getProductStatsByTrademark(@Param("date") int date, @Param("limit") int limit);
    
    // 获取某一天不同品类的交易额
    List<ProductStats> getProductStatsByCategory3(@Param("date") int date, @Param("limit") int limit);
    
    // 获取某一天不同SPU的交易额
    List<ProductStats> getProductStatsBySPU(@Param("date") int date, @Param("limit") int limit);
}