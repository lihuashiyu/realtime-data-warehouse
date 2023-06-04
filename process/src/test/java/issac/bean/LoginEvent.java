package issac.bean;

/**
 * ********************************************************************
 * ProjectName   ：  test
 * Package       ：  com.yuanwang
 * ClassName     ：  LoginEvent
 * CreateTime    ：  2023-04-07 14:14
 * Author        ：  Issac_Al
 * Email         ：  IssacAl@qq.com
 * IDE           ：  IntelliJ IDEA 2020.3.4
 * Version       ：  1.0
 * CodedFormat   ：  utf-8
 * Description   ：  LoginEvent 被用于 ==>
 * ********************************************************************
 */

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 用户登录的数据：user, ip, type
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginEvent implements Serializable
{
    private static final long serialVersionUID = 4611686018427387904L;
    
    private int userId;
    private String userName;
    private int age;
    private int score;
    private int flow;
    private String loginStatus;
    private long order;
    private Long loginTime;
}
