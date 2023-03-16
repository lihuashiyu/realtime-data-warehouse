package issac.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class TradeOrderBean
{
    String stt;                                  // 窗口起始时间
    String edt;                                  // 窗口关闭时间
    Long orderUniqueUserCount;                   // 下单独立用户数
    Long orderNewUserCount;                      // 下单新用户数
    Double orderActivityReduceAmount;            // 下单活动减免金额
    Double orderCouponReduceAmount;              // 下单优惠券减免金额
    Double orderOriginalTotalAmount;             // 下单原始金额
    Long ts;                                     // 时间戳
}
