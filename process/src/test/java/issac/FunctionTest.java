package issac;

import issac.annotation.Parameter;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * ********************************************************************
 * ProjectName   ：  realtime
 * Package       ：  issac.test
 * ClassName     ：  FunctionTest
 * CreateTime    ：  2022-11-23 23:01
 * Author        ：  Issac_Al
 * Email         ：  IssacAl@qq.com
 * IDE           ：  IntelliJ IDEA 2020.3.4
 * Version       ：  1.0
 * CodedFormat   ：  utf-8
 * Description   ：  FunctionTest 被用于 ==>
 * ********************************************************************
 */
@Slf4j
public class FunctionTest
{
    public void paramTest(@Parameter(value = "id") String id, @Parameter(value = "name") String name)
    {
        Method paramTest = null;
        try
        {
            paramTest = this.getClass().getMethod("paramTest");
        } catch (NoSuchMethodException e)
        {
            e.printStackTrace();
        }
        
        // Map<String, String> map = annotationUtil.parameterValue(paramTest);
        Map<String, String> map = new HashMap<>();
    
        log.info("{}", map);
    }
}
