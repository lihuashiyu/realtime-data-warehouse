package issac.mapper;

import issac.annotation.Mapper;
import issac.annotation.Method;
import issac.utils.XmlUtil;

/**
 * ********************************************************************
 * ProjectName   ：  realtime
 * Package       ：  issac.mapper
 * ClassName     ：  DWD
 * CreateTime    ：  2022-11-25 21:48
 * Author        ：  lihuashiyu
 * Email         ：  lihuashiyu@github.com
 * IDE           ：  IntelliJ IDEA 2020.3.4
 * Version       ：  1.0
 * CodedFormat   ：  utf-8
 * Description   ：  DWD 被用于 ==> 调用 xml 中的 sql
 * ********************************************************************
 */
@Mapper(value = "mapper.dwd")
public class DWD
{
    /**
     * 获取 @Mapper 注解对应的注解 中 与 @Method 注解相同的节点的 sql 
     * 
     * @param name      ： 参数
     * @param age       ： 参数
     * @return          ： sql
     */
    @Method(value = "createTopicDb")
    public String createTopicDb()
    {
        return XmlUtil.parser();
    }
    
    
    @Method(value = "")
}
