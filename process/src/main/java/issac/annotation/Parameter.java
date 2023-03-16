package issac.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * ********************************************************************
 * ProjectName   ：  realtime
 * Package       ：  issac
 * InterfaceName ：  Issac
 * CreateTime    ：  2022-11-19 21:16
 * Author        ：  Issac_Al
 * Email         ：  IssacAl@qq.com
 * IDE           ：  IntelliJ IDEA 2020.3.4
 * Version       ：  1.0
 * CodedFormat   ：  utf-8
 * Description   ：  Issac 被用于 ==>
 * ********************************************************************
 */
@Documented
@Target({ElementType.PARAMETER, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Parameter
{
    String value()  default "";
}
