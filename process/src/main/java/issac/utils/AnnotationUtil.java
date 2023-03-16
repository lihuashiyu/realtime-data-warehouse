package issac.utils;

import cn.hutool.core.util.StrUtil;
import issac.annotation.Attribute;
import issac.annotation.Mapper;
import issac.annotation.Method;
import issac.annotation.Parameter;
import issac.constant.AnnotationConstant;
import issac.constant.NumberConstant;
import issac.constant.SignalConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * ********************************************************************
 * ProjectName   ：  realtime
 * Package       ：  issac.utils
 * ClassName     ：  AnnotationUtil
 * CreateTime    ：  2022-11-19 21:33
 * Author        ：  Issac_Al
 * Email         ：  IssacAl@qq.com
 * IDE           ：  IntelliJ IDEA 2020.3.4
 * Version       ：  1.0
 * CodedFormat   ：  utf-8
 * Description   ：  AnnotationUtil 被用于 ==>
 * ********************************************************************
 */
@Slf4j
public class AnnotationUtil
{
    /**
     * 根据反射，给字段赋值
     * 
     * @param t             ： 类
     * @param fieldValueMap ： 字段和值的映射
     * @param <T>           ： 返回值
     */
    public static <T> void fieldValueByAnnotation(T t, Map<String, Object> fieldValueMap)
    {
        Class<?> clazz = t.getClass();                                         // 获取类名
        Field[] fields = clazz.getDeclaredFields();                            // 获取类的所有字段
        Class<Attribute> fieldNameClass = Attribute.class;                     // 获取字段名
        
        // 遍历属性，并给属性赋值
        for (Field field : fields)
        {
            // 若属性上有注解，使用注解的值作为 key 给字段赋值
            if (field.isAnnotationPresent(fieldNameClass))
            {
                String key = field.getAnnotation(fieldNameClass).value();      // 获取注解的值
                Object value = fieldValueMap.get(key);                         // 获取属性值
                field.setAccessible(true);                                     // 修改属性
                
                try { field.set(t, value); } 
                catch (IllegalArgumentException | IllegalAccessException e)
                {
                    log.error("类 {} 的属性字段 {} 的类型 {} 与 所赋值 {} 的类型 {} 不同，将默认为 null", 
                        clazz.getName(), field.getName(), field.getType(), value, value.getClass());
                }
            }
        }
    }
    
    
    /**
     * 获取类上注解为 @Mapper 的相关注解值
     * 
     * @param clazz                  ： 实体对象
     * @param objects                ： 方法入参的参数类型
     * @return                       ： 获取类上注解为 @Mapper 的相关注解值
     */
    public static Map<String, String> getMapperAnnotation(Class<?> clazz, Object... objects)
    {
        Map<String, String> xmlMap = new HashMap<>();
        
        String fileName = clazz.getAnnotation(Mapper.class).value();
        fileName = fileName.replace(SignalConstant.DOT, SignalConstant.FORWARD_SLASH);
        String xmlPath = StrUtil.format("{}.{}", fileName, AnnotationConstant.XML_FORMAT);
        
        String nodeName = SignalConstant.EMPTY;
        for (java.lang.reflect.Method method : clazz.getMethods())
        {
            if (method.isAnnotationPresent(Method.class))
            {
                nodeName = method.getAnnotation(Method.class).value();
                
                Annotation[][] annotations = method.getParameterAnnotations();
                if (ArrayUtils.isNotEmpty(annotations))
                {
                    for (int i = 0; i < annotations.length; i++)
                    {
                        if (annotations[i][NumberConstant.ZERO] instanceof Parameter)
                        {
                            String param = ((Parameter) annotations[i][NumberConstant.ZERO]).value();
                            xmlMap.put(param, String.valueOf(objects[i]));
                        } else
                        {
                            log.warn("类 {} 中 方法 {} 的参数 {} 没有注解", clazz, method, objects[i]);
                        }
                    }
                }
            }
        }
        
        xmlMap.put(AnnotationConstant.XML_FILE_NAME, xmlPath);
        xmlMap.put(AnnotationConstant.XML_NODE_NAME, nodeName);
        
        return xmlMap;
    }
}
