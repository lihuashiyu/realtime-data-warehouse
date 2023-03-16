package issac.bean;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CartAddUuBean
{
    String stt;                                  // 窗口起始时间
    String edt;                                  // 窗口闭合时间
    Long cartAddUuCt;                            // 加购独立用户数
    Long ts;                                     // 时间戳
}

