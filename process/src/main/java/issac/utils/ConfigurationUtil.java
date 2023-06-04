package issac.utils;

import cn.hutool.core.util.StrUtil;
import issac.constant.ApplicationConstant;
import issac.constant.NumberConstant;
import issac.constant.SignalConstant;
import issac.constant.UtilConstant;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.java.utils.ParameterTool;
import org.yaml.snakeyaml.Yaml;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * ********************************************************************
 * ProjectName   ：  realtime
 * Package       ：  issac.utils
 * ClassName     ：  PropertyUtil
 * CreateTime    ：  2022-11-17 23:06
 * Author        ：  lihuashiyu
 * Email         ：  lihuashiyu@github.com
 * IDE           ：  IntelliJ IDEA 2020.3.4
 * Version       ：  1.0
 * CodedFormat   ：  utf-8
 * Description   ：  类描述：解析 properties 文件工具类
 * ********************************************************************
 */
@Slf4j
public class ConfigurationUtil
{
    /**
     * 解析配置文件路径的 application.properties 配置文件
     *
     * @return     ： properties 配置文件
     */
    @SneakyThrows
    public static ParameterTool getProperties()
    {
        InputStream inputStream = parseFile(ApplicationConstant.PROPERTY_CONFIG_FILE_NAME);
        return ParameterTool.fromPropertiesFile(inputStream);
    }
    
    
    /**
     * 根据文件名解析 文本文件
     * 
     * @param configFileName      ： 文件名称
     * @return                    ： 文件输入库
     * @throws IOException        ： IO 异常
     */
    public static InputStream parseFile(String configFileName) throws IOException
    {
        // 获取当前 jar 绝对路径下可使用的的配置文件
        String propertyPath = getConfigAbsolutePath(configFileName);
        
        // 初始化输入流
        InputStream inputStream;
        
        // 判断外部配置文件是否存在，若不存在就是用 jar 包内部的配置文件
        if (StringUtils.isBlank(propertyPath))
        {
            // 使用 InPutStream 流读取 properties 文件（properties 文件默认放在 resource 目录下）
            inputStream = ConfigurationUtil.class.getClassLoader().getResourceAsStream(configFileName);
            log.warn("未在系统中找到文件名为 {} 的配置文件，将使用默认配置 ", configFileName);
        
            String configPath = getServiceDirectory();                              // jar 包所在的绝对路径
            log.warn("若使用外部配置文件，可将文件名为 {} 的配置文件放在服务安装路径的 conf/ 或 config/ 文件夹下， ", configFileName);
            log.warn("    即绝对路径为：{}/config/ 或 {}/conf/ 路径下 ...... ", configPath, configPath);
        } else
        {
            // 使用 jar 包外部的 properties 配置文件文件
            inputStream = new BufferedInputStream(new FileInputStream(propertyPath));
            log.info("使用的配置文件路径为：{}", propertyPath);
        }
        
        return inputStream;
    }
    
    
    /**
     * 根据文件名解析 properties 文件
     *
     * @param configFileName ： properties 文件名
     * @return ： 文件对应的 键值对
     *
     * @throws IOException ： 文件不存在等异常
     */
    public static Map<String, String> parseProperty(String configFileName) throws IOException
    {
        InputStream inputStream = parseFile(configFileName);
    
        Properties properties = new Properties();
        properties.load(inputStream);
        inputStream.close();
        
        // 将配置文件中的 key、value 转换为 Map，并返回
        return new HashMap(properties);
    }
    
    
    /**
     * 解析 application.properties 配置文件
     *
     * @return ： application 配置文件的键值对
     *
     * @throws IOException ：  文件不存在等异常
     */
    public static Map<String, String> parseApplicationProperty() throws IOException
    {
        return parseProperty(ApplicationConstant.PROPERTY_CONFIG_FILE_NAME);
    }
    
    
    /**
     * 根据文件名解析 yml 文件
     *
     * @param configFileName ： yml 配置文件名
     * @return ： 文件对应的 键值对
     *
     * @throws IOException ： 文件不存在等异常
     */
    public static Map<String, Object> parseYaml(String configFileName) throws IOException
    {
        InputStream inputStream = parseFile(configFileName);
        
        Yaml yaml = new Yaml();
        Map<String, Object> load = yaml.load(inputStream);
        inputStream.close();
        
        return load;
    }
    
    
    /**
     * 解析 application.yml 配置文件
     *
     * @return ： application 配置文件的键值对
     *
     * @throws IOException ： 文件不存在等异常
     */
    public Map<String, Object> parseApplicationYaml() throws IOException
    {
        return parseYaml(ApplicationConstant.YAML_CONFIG_FILE_NAME);
    }
    
    
    /**
     * 获取当前 jar 的绝对路径下可能存在的配置文件路径：
     *
     * <p> 优先级：../config、../conf/、./、config/、conf/  </p>
     *
     * @return 配置文件的绝对路径
     */
    public static String getConfigAbsolutePath(String fileName)
    {
        String configFile = null;
    
        String serviceDirectory = getServiceDirectory();
        String parentDirectory = getServiceParentDirectory();
        
        // 配置 外部 配置文件的路径，数字越小，优先级越高
        Map<Integer, String> configMap = new HashMap<>();
        configMap.put(1, StrUtil.format("{}/{}/{}", parentDirectory, UtilConstant.CONFIG_DIRECTORY, fileName));
        configMap.put(2, StrUtil.format("{}/{}/{}", parentDirectory, UtilConstant.CONF_DIRECTORY, fileName));
        configMap.put(3, StrUtil.format("{}/{}", serviceDirectory, fileName));
        configMap.put(4, StrUtil.format("{}/{}/{}", serviceDirectory, UtilConstant.CONFIG_DIRECTORY, fileName));
        configMap.put(5, StrUtil.format("{}/{}/{}", serviceDirectory, UtilConstant.CONF_DIRECTORY, fileName));
        
        for (int i = 1; i <= configMap.size(); i++)
        {
            File fileAbsPath = new File(configMap.get(i));
            
            // 如果路径存在，而且确实是文件
            if (fileAbsPath.exists() && fileAbsPath.isFile())
            {
                configFile = configMap.get(i);
                break;
            }
        }
        
        return configFile;
    }
    
    
    /**
     * 获取当前 服务 包所在路径的绝对路径
     *
     * @return ： 获取当前 服务 包所在路径的绝对路径
     */
    @SneakyThrows
    public static String getServiceDirectory()
    {
        String classPath = ConfigurationUtil.class.getProtectionDomain().getCodeSource().getLocation().getFile();
        classPath = URLDecoder.decode(classPath, UtilConstant.FILE_ENCODING);
        return new File(classPath).getParentFile().getAbsolutePath();
    }
    
    
    /**
     * 获取当前 服务 包所在路径的父目录的绝对路径
     *
     * @return ： 获取当前 服务 包所在路径的父目录的绝对路径
     */
    public static String getServiceParentDirectory()
    {
        // 获取当前 服务 包所在路径的绝对路径
        String serviceDirectory = getServiceDirectory();
    
        String parentDirectory;
        if (StringUtils.equals(SignalConstant.FORWARD_SLASH, serviceDirectory))
        {
            log.warn("文件所在的路径为根目录，没有上级目录");
            parentDirectory = serviceDirectory;
        } else 
        {
            int slashIndex = serviceDirectory.lastIndexOf(SignalConstant.FORWARD_SLASH);
            parentDirectory = serviceDirectory.substring(NumberConstant.ZERO, slashIndex);
        }
        
        return parentDirectory;
    }
}
