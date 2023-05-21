package issac.utils;

import com.alibaba.fastjson.JSONObject;
import issac.constant.ConfigConstant;
import issac.mapper.DWD;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.configuration2.XMLConfiguration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.tree.DefaultExpressionEngine;
import org.apache.commons.configuration2.tree.DefaultExpressionEngineSymbols;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Map;

/**
 * ********************************************************************
 * ProjectName   ：  realtime
 * Package       ：  issac.test
 * ClassName     ：  UtilsTest
 * CreateTime    ：  2022-11-17 23:12
 * Author        ：  Issac_Al
 * Email         ：  IssacAl@qq.com
 * IDE           ：  IntelliJ IDEA 2020.3.4
 * Version       ：  1.0
 * CodedFormat   ：  utf-8
 * Description   ：  UtilsTest 被用于 ==>
 * ********************************************************************
 */
@Slf4j
public class UtilsTest
{
    @Test
    @SneakyThrows
    public void propertyTest()
    {
        Map<String, String> map = ConfigurationUtil.parseProperty(ConfigConstant.PROPERTY_CONFIG_FILE_NAME);
        map.forEach((key, value) -> log.info("{} <===> {}", key, value));
    }
    
    
    @Test
    @SneakyThrows
    public void yamlTest()
    {
        Map<String, Object> map = ConfigurationUtil.parseYaml(ConfigConstant.YAML_CONFIG_FILE_NAME);
        map.forEach((key, value) -> log.info("{} <===> {} ", key, value));
    
        log.info("mysql <===> {}, type: {} ", map.get("mysql"), map.get("mysql").getClass());
        log.info("url <===> {}, type: {} ", map.get("url"), map.get("url").getClass());
    }
    
    
    @Test
    public void xmlTest()
    {
        JSONObject xmlToJson = XmlUtil.xmlToJson("mapper/dwd.xml", "Mapper");
        Object topicDb = xmlToJson.get("createTopicDb");
        log.info("{} ", topicDb);
    }
    
    
    @Test
    public void dwdTest()
    {
        DWD dwd = new DWD();
        String topicDb = dwd.createTopicDb();
        log.info("topic_db = {}", topicDb);
    }
    
    
    @Test
    @SneakyThrows
    public void configTest()
    {
        Configurations config = new Configurations();
        URL resource = UtilsTest.class.getClassLoader().getResource("hbase-site.xml");
        XMLConfiguration xml = config.xml("hbase-site.xml");
        
        DefaultExpressionEngine engine = new DefaultExpressionEngine(DefaultExpressionEngineSymbols.DEFAULT_SYMBOLS);
        xml.setExpressionEngine(engine);
        log.info(xml.getString("configuration"));
        
        // Document document = xmlConfiguration.getDocument();
        // NodeList property = document.getElementsByTagName("property");
        // log.info(" property = {}", property);
    }
    
    
    @SneakyThrows
    @Test
    public void parserFileTest()
    {
        InputStream resource = UtilsTest.class.getClassLoader().getResourceAsStream("hbase-site.xml");
        
        assert resource != null;
        BufferedReader br = new BufferedReader(new InputStreamReader(resource));
        String s = null;
        while ((s = br.readLine()) != null)
        {
            log.info("{}", s);
        }
    }
    
    
    @Test
    public void path()
    {
        String path = ConfigurationUtil.getServiceDirectory();
        log.info("path = {}", path);
    }
    
    
    @Test
    public void md5Test()
    {
        log.info("OS = {}",SystemUtil.getSystemName());
        
        SystemUtil.MD5("/home/issac/视频/HD-lzdq-009.mp4");
        SystemUtil.MD5("logs/info.logs");
    }
}
