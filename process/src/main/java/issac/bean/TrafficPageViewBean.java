package issac.bean;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TrafficPageViewBean
{
    String stt;                                  // 窗口起始时间
    String edt;                                  // 窗口结束时间
    String vc;                                   // app 版本号
    String ch;                                   // 渠道
    String ar;                                   // 地区
    String isNew;                                // 新老访客状态标记
    Long uvCt;                                   // 独立访客数
    Long svCt;                                   // 会话数
    Long pvCt;                                   // 页面浏览数
    Long durSum;                                 // 累计访问时长
    Long ujCt;                                   // 跳出会话数
    Long ts;                                     // 时间戳
}