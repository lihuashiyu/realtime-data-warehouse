package issac.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * **************************************************************************************************
 * ProjectName   ：  realtime
 * Package       ：  issac.bean
 * ClassName     ：  MysqlCdcBean
 * CreateTime    ：  2023-06-05 21:41
 * Author        ：  Issac_Al
 * Email         ：  IssacAl@qq.com
 * IDE           ：  IntelliJ IDEA 2020.3.4
 * Version       ：  1.0
 * CodedFormat   ：  utf-8
 * Description   ：  Java Class
 * **************************************************************************************************
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MysqlCdcBean<T>
{
    private String database;
    private String tableName;
    private String operateType;
    private T before;
    private T after;
    private Long timestamp;
}
