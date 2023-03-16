package issac.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@AllArgsConstructor
@Builder
public class TradeProvinceOrderWindow
{
    String stt;                                  // 窗口起始时间
    String edt;                                  // 窗口结束时间
    String provinceId;                           // 省份 ID
    @Builder.Default
    String provinceName = "";                    // 省份名称
    Long orderCount;                             // 累计下单次数
    @TransientSink
    Set<String> orderIdSet;                      // 订单 ID 集合，用于统计下单次数
    Double orderAmount;                          // 累计下单金额
    Long ts;                                     // 时间戳
}
