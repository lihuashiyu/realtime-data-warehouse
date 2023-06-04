package issac.utils;

import issac.constant.ApplicationConstant;
import issac.constant.DwdConstant;
import issac.mapper.MysqlDDL;
import lombok.SneakyThrows;

import java.util.Map;

public class MysqlUtil
{
    @SneakyThrows
    public static String getBaseDicLookUpDDL()
    {
        Map<String, String> configMap = ConfigurationUtil.parseProperty(ApplicationConstant.PROPERTY_CONFIG_FILE_NAME);
        String driverName = configMap.get(ApplicationConstant.MYSQL_DRIVER);
        String url = configMap.get(ApplicationConstant.MYSQL_CONNECTION_URL);
        String userName = configMap.get(ApplicationConstant.MYSQL_USER);
        String passWord = configMap.get(ApplicationConstant.MYSQL_PASSWORD);
        String maxRows = configMap.get(ApplicationConstant.FLINK_CDC_MYSQL_MAX_ROWS);
        String ttl = configMap.get(ApplicationConstant.FLINK_CDC_MYSQL_TTL);
        
        MysqlDDL mysql = new MysqlDDL();
        String tableName = DwdConstant.DWD_TABLE_BASE_DIC;
        
        return mysql.createBaseDic(driverName, url, userName, passWord, tableName, maxRows, ttl);
    }
}