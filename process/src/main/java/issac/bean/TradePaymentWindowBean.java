package issac.bean;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TradePaymentWindowBean
{
    String stt;                                  // 窗口起始时间
    String edt;                                  // 窗口终止时间
    Long paymentSucUniqueUserCount;              // 支付成功独立用户数
    Long paymentSucNewUserCount;                 // 支付成功新用户数
    Long ts;                                     // 时间戳
}
