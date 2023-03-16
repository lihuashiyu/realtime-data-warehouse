package issac.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TradeUserSpuOrderBean
{
    String stt;                                  // 窗口起始时间
    String edt;                                  // 窗口结束时间
    String trademarkId;                          // 品牌 ID
    String trademarkName;                        // 品牌名称
    String category1Id;                          // 一级品类 ID
    String category1Name;                        // 一级品类名称
    String category2Id;                          // 二级品类 ID
    String category2Name;                        // 二级品类名称
    String category3Id;                          // 三级品类 ID
    String category3Name;                        // 三级品类名称
    
    @TransientSink
    Set<String> orderIdSet;                      // 订单 ID
    
    @TransientSink
    String skuId;                                // sku_id
    
    String userId;                               // 用户 ID
    String spuId;                                // spu_id
    String spuName;                              // spu 名称
    Long orderCount;                             // 下单次数
    Double orderAmount;                          // 下单金额
    Long ts;                                     // 时间戳
}
