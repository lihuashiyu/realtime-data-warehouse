package issac.utils;

import issac.constant.NumberConstant;
import issac.constant.SystemConstant;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;

/**
 * ********************************************************************
 * ProjectName   ：  realtime
 * Package       ：  issac.utils
 * ClassName     ：  FileUtil
 * CreateTime    ：  2022-12-29 21:39
 * Author        ：  lihuashiyu
 * Email         ：  lihuashiyu@github.com
 * IDE           ：  IntelliJ IDEA 2020.3.4
 * Version       ：  1.0
 * CodedFormat   ：  utf-8
 * Description   ：  FileUtil 被用于 ==>
 * ********************************************************************
 */
@Slf4j
public class SystemUtil
{
    /**
     * 获取文件或字符串的 MD5
     * 
     * @param fileName  ： 文件路径 或 字符串
     * @return          ： MD5 值
     */
    @SneakyThrows
    public static String MD5(String fileName)
    {
        String md5String;
        
        // 获取文件的绝对路径
        String absolutePath = getFileAbsolutePath(fileName);
        
        // 判断文件是否存在
        if (StringUtils.isNotBlank(absolutePath))
        {
            MessageDigest MD5 = MessageDigest.getInstance(SystemConstant.MD5);
            FileInputStream fileInputStream = new FileInputStream(absolutePath);
            byte[] buffer = new byte[SystemConstant.FILE_CACHE];
            
            int length;
            while ((length = fileInputStream.read(buffer)) != NumberConstant.MINUS_ONE)
            {
                MD5.update(buffer, NumberConstant.ZERO, length);
            }
        
            md5String = new String(Hex.encodeHex(MD5.digest()));
            fileInputStream.close();
            log.info("文件的 MD5 为：MD5 = {}", md5String);
        } else
        {
            md5String = DigestUtils.md5Hex(fileName);
            log.info("字符串的 MD5 为：MD5 = {}", md5String);
        }
        
        return md5String;
    }
    
    
    /**
     * 获取系统名称
     * @return     ： 系统名称
     */
    public static String getSystemName()
    {
        String os = System.getProperty("os.name").toLowerCase();
    
        String osName = SystemConstant.OTHER;
        if (os.contains("linux"))
        {
            osName = SystemConstant.LINUX;
        } else if (os.contains("mac") && os.indexOf("os") > 0)
        {
            osName = SystemConstant.MAC;
        } else if (os.contains("windows"))
        {
            osName = SystemConstant.WINDOWS;
        } else if (os.contains("os/2")) 
        {
            osName = SystemConstant.OS2;
        } else if (os.contains("solaris"))
        {
            osName = SystemConstant.SOLARIS;
        } else if (os.contains("sunos"))
        {
            osName = SystemConstant.SUN;
        } else if (os.contains("mpe/ix"))
        {
            osName = SystemConstant.MPEIX;
        } else if (os.contains("hp-ux"))
        {
            osName = SystemConstant.HP_UX;
        } else if (os.contains("aix"))
        {
            osName = SystemConstant.AIX;
        } else if (os.contains("os/390"))
        {
            osName = SystemConstant.OS_390;
        } else if (os.contains("freebsd"))
        {
            osName = SystemConstant.FREE_BSD;
        } else if (os.contains("irix"))
        {
            osName = SystemConstant.IRIX;
        } else if (os.contains("digital") && os.indexOf("unix") > 0)
        {
            osName = SystemConstant.DIGITAL_UNIX;
        } else if (os.contains("netware"))
        {
            osName = SystemConstant.NETWARE_411;
        } else if (os.contains("osf1"))
        {
            osName = SystemConstant.OSF_1;
        } else if (os.contains("openvms"))
        {
            osName = SystemConstant.OPEN_VMS;
        }
        
        return osName;
    }
    
    
    /**
     * 判断文件是否存在，若存在则获取文件的绝对路径，不存在则返回空字符串
     * 
     * @param fileName  ： 文件路径
     * @return          ： 文件的绝对路径
     */
    public static String getFileAbsolutePath(String fileName)
    {
        String absolutePath = "";
        
        File file = new File(fileName);
        if (file.exists() && file.isFile())
        {
            absolutePath = file.getAbsolutePath();
        } else
        {
            log.warn("文件 {} 不存在 ...... ", fileName);
        }
        
        return absolutePath;
    }
}
