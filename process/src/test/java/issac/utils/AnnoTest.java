package issac.utils;

import issac.bean.BeanTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

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
    @Test
    public void fieldTest()
    {
        Map<String, Object> map = new HashMap<>();
        map.put("id", 1000L);
        map.put("name", "issac");
        map.put("age", 29);
        map.put("gender", 1);
        map.put("mark", null);
        
        BeanTest bean = new BeanTest();
        AnnotationUtil.fieldValueByAnnotation(bean, map);
        
        log.info("bean = {}", bean);
    }
}
