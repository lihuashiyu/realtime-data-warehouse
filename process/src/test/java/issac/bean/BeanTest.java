package issac.bean;

import issac.annotation.Attribute;
import lombok.Data;

/**
 * ********************************************************************
 * ProjectName   ：  realtime
 * Package       ：  issac.test
 * ClassName     ：  BeanTest
 * CreateTime    ：  2022-11-19 21:28
 * Author        ：  Issac_Al
 * Email         ：  IssacAl@qq.com
 * IDE           ：  IntelliJ IDEA 2020.3.4
 * Version       ：  1.0
 * CodedFormat   ：  utf-8
 * Description   ：  BeanTest 被用于 ==>
 * ****************************************************************************
 */
@Data
public class BeanTest
{
    @Attribute(value = "id", useful = 1)
    private Long id;
    
    @Attribute(value = "name", useful = 1)
    private String name;
    
    @Attribute(value = "age", useful = 1)
    private int age;
    
    @Attribute(value = "gender", useful = 1)
    private int gender;
    
    @Attribute(value = "mark", useful = 1)
    private String mark;
    
    @Attribute(value = "other", useful = 0)
    private String other;
}
