package issac;

import issac.utils.AnnotationUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * ********************************************************************
 * ProjectName   ：  realtime
 * Package       ：  issac.test
 * ClassName     ：  AnnoTest
 * CreateTime    ：  2022-11-19 21:32
 * Author        ：  Issac_Al
 * Email         ：  IssacAl@qq.com
 * IDE           ：  IntelliJ IDEA 2020.3.4
 * Version       ：  1.0
 * CodedFormat   ：  utf-8
 * Description   ：  AnnoTest 被用于 ==>
 * ********************************************************************
 */
@Slf4j
public class AnnoTest
{
    public static void main(String[] args)
    {
        fieldTest();
        // paramTest();
    }
    
    
    private static void fieldTest()
    {
        Map<String, Object> map = new HashMap<>();
        map.put("id", 1000L);
        map.put("name", "issac");
        map.put("age", 29);
        map.put("gender", 1);
        map.put("mark", null);
        
        BeanTest bean = new BeanTest();
        AnnotationUtil.fieldValueByAnnotation(bean, map);
        
        log.info("{}", bean);
    }
    
    
    @SneakyThrows
    private static void paramTest()
    {
        FunctionTest functionTest = new FunctionTest();
        functionTest.getClass().getMethod("paramTest");
        
        functionTest.paramTest("100", "issac");
        
    }
}
