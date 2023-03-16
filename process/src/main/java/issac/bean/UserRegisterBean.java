package issac.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRegisterBean
{
    String stt;                                  // 窗口起始时间
    String edt;                                  // 窗口终止时间
    Long registerCt;                             // 注册用户数
    Long ts;                                     // 时间戳
}
