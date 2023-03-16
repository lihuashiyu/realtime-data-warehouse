package issac.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginBean 
{
    String stt;                                  // 窗口起始时间
    String edt;                                  // 窗口终止时间
    Long backCt;                                 // 回流用户数
    Long uuCt;                                   // 独立用户数
    Long ts;                                     // 时间戳
}

