package issac.bean;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TrafficHomeDetailPageViewBean
{
    String stt;                                  // 窗口起始时间
    String edt;                                  // 窗口结束时间
    Long homeUvCt;                               // 首页独立访客数
    Long goodDetailUvCt;                         // 商品详情页独立访客数
    Long ts;                                     // 时间戳
}