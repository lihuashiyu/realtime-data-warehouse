package issac.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * ********************************************************************
 * ProjectName   ：  realtime
 * Package       ：  issac.annotation
 * InterfaceName ：  Mapper
 * CreateTime    ：  2022-11-25 21:41
 * Author        ：  lihuashiyu
 * Email         ：  lihuashiyu@github.com
 * IDE           ：  IntelliJ IDEA 2020.3.4
 * Version       ：  1.0
 * CodedFormat   ：  utf-8
 * Description   ：  Mapper 被用于 ==>
 * ********************************************************************
 */
@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Mapper
{
    String value()  default "";
}
