package issac.utils;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import issac.constant.AnnotationConstant;
import issac.constant.NumberConstant;
import issac.constant.SignalConstant;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.InputStream;
import java.io.StringReader;
import java.util.List;
import java.util.Map;

/**
 * ********************************************************************
 * ProjectName   ：  realtime
 * Package       ：  issac.utils
 * ClassName     ：  XmlUtil
 * CreateTime    ：  2022-11-06 14:06
 * Author        ：  lihuashiyu
 * Email         ：  lihuashiyu@github.com
 * IDE           ：  IntelliJ IDEA 2020.3.4
 * Version       ：  1.0
 * CodedFormat   ：  utf-8
 * Description   ：  XmlUtil 被用于 ==>
 * ********************************************************************
 */

//  xml 解析
@Slf4j
public class XmlUtil
{
    /**
     * XML节点转换 JSON 对象
     * 
     * @param element ： 节点
     * @param object  ： 新的 JSON 存储
     * @return        ： JSON 对象
     */
    private static JSONObject xmlToJson(Element element, JSONObject object)
    {
        List<Element> elements = element.elements();
        for (Element child : elements)
        {
            Object value = object.get(child.getName());
            Object newValue;
            
            if (child.elements().size() > 0)
            {
                JSONObject jsonObject = xmlToJson(child, new JSONObject(true));
                if (!jsonObject.isEmpty()) { newValue = jsonObject; } 
                else { newValue = child.getText(); }
            } else
            {
                newValue = child.getText();
            }
            
            List<Attribute> attributes = child.attributes();
            if (!attributes.isEmpty())
            {
                JSONObject attrJsonObject = new JSONObject();
                for (Attribute attribute : attributes)
                {
                    attrJsonObject.put(attribute.getName(), attribute.getText());
                    attrJsonObject.put("content", newValue);
                }
                
                newValue = attrJsonObject;
            }
            
            if (newValue != null)
            {
                if (value != null)
                {
                    if (value instanceof JSONArray)
                    {
                        ((JSONArray) value).add(newValue);
                    } else
                    {
                        JSONArray array = new JSONArray();
                        array.add(value);
                        array.add(newValue);
                        object.put(child.getName(), array);
                    }
                } else
                {
                    object.put(child.getName(), newValue);
                }
            }
        }
        
        return object;
    }
    
    
    /**
     * XML 字符串转换JSON对象
     * 
     * @param xmlStr ： XML字符串
     * @return       ： JSON 对象
     */
    public static JSONObject xmlToJson(String xmlStr)
    {
        JSONObject result = new JSONObject(true);
        SAXReader xmlReader = new SAXReader();
        try
        {
            Document document = xmlReader.read(new StringReader(xmlStr));
            Element element = document.getRootElement();
            return xmlToJson(element, result);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return result;
    }
    
    
    /**
     * XML 文件转换 JSON 对象
     * 
     * @param filePath ： 文件路径
     * @param node     ： 选择节点
     * @return         ： JSON 对象
     */
    public static JSONObject xmlToJson(String filePath, String node)
    {
        InputStream resource = XmlUtil.class.getClassLoader().getResourceAsStream(filePath);
        JSONObject result = new JSONObject(true);
        SAXReader xmlReader = new SAXReader();
        
        try
        {
            Document document = xmlReader.read(resource);
            Element element;
            if (StringUtils.isBlank(node)) { element = document.getRootElement(); } 
            else { element = (Element) document.selectSingleNode(node); }
            
            return xmlToJson(element, result);
        } catch (Exception e)
        {
            log.error("输入的文件路径 {} 错误", filePath);
            e.printStackTrace();
        }
        
        return result;
    }
    
    
    /**
     * 将 xml 的 sql 中的变量，替换为真实值
     * 
     * @param sql           ： xml 中的 sql 
     * @param variableMap   ： xml 中 sql 对应的 变量 ${v} 与真实值之间的映射
     * @return              ： 变量替换后的 sql
     */
    public static String replaceVariable(String sql, Map<String, String> variableMap)
    {
        for (Map.Entry<String, String> entry : variableMap.entrySet())
        {
            String key = entry.getKey();
            String value = entry.getValue();
    
            String variable = StrUtil.format("${{}}", key);
            sql = sql.replace(variable, value);
        }
        
        return sql.replace(StrUtil.LF, SignalConstant.EMPTY);
    }
    
    
    /**
     * 根据注解获取 resurce 中 mapper 下的 xml 中的 sql
     * 
     * @param objects    ： 参数
     * @return           ： xml 中的 sql
     */
    @SneakyThrows
    public static String parser(Object... objects)
    {
        StackTraceElement[] stackTraceArray = Thread.currentThread().getStackTrace();
    
        String className = stackTraceArray[NumberConstant.TWO].getClassName();
        Class<?> clazz = Class.forName(className);
        
        Map<String, String> annotationMap = AnnotationUtil.getMapperAnnotation(clazz, objects);
        String xmlPath = annotationMap.get(AnnotationConstant.XML_FILE_NAME);
        String xmlNode = annotationMap.get(AnnotationConstant.XML_NODE_NAME);
        
        JSONObject sqlJson = xmlToJson(xmlPath, AnnotationConstant.XML_ROOT_NODE);
        String sqlTemp = sqlJson.getObject(xmlNode, String.class);
        if (StringUtils.isBlank(sqlTemp))
        {
            log.warn("文件 {} 中 不存在节点 {}，将返回空字符串 ", xmlPath, xmlNode);
            return SignalConstant.EMPTY;
        }
        
        return replaceVariable(sqlTemp, annotationMap);   
    }
}
