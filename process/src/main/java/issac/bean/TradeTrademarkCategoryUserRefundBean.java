package issac.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

@Data
@AllArgsConstructor
@Builder
@Slf4j
public class TradeTrademarkCategoryUserRefundBean
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
    Long refundCount;                            // 退单次数
    Long ts;                                     // 时间戳
    
    public static void main(String[] args)
    {
        TradeTrademarkCategoryUserRefundBean build = builder().build();
        log.info("{}", build);
    }
}
