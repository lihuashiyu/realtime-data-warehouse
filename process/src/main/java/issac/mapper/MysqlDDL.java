package issac.mapper;

import issac.annotation.Mapper;
import issac.annotation.Method;
import issac.annotation.Parameter;
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
@Mapper(value = "mapper.mysql")
public class MysqlDDL
{
    /**
     * 获取 @Mapper 注解对应的注解 中 与 @Method 注解相同的节点的 sql 
     * 
     * @param name      ： 参数
     * @param age       ： 参数
     * @return          ： sql
     */
    @Method(value = "CreateTableBaseDic")
    public String createBaseDic(@Parameter("driverName") String driverName,
                                @Parameter("url") String url, 
                                @Parameter("userName") String userName, 
                                @Parameter("passWord") String passWord, 
                                @Parameter("tableName") String tableName, 
                                @Parameter("maxRows") String maxRows,
                                @Parameter("ttl") String ttl)
    {
        return XmlUtil.parser();
    }
}
