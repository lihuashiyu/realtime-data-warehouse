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
 * InterfaceName ：  Method
 * CreateTime    ：  2022-11-25 21:43
 * Author        ：  lihuashiyu
 * Email         ：  lihuashiyu@github.com
 * IDE           ：  IntelliJ IDEA 2020.3.4
 * Version       ：  1.0
 * CodedFormat   ：  utf-8
 * Description   ：  Method 被用于 ==>
 * ********************************************************************
 */
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Method
{
    String value()  default "";
}
