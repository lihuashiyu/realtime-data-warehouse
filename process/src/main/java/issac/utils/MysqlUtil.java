package issac.utils;

import issac.constant.ConfigConstant;
import issac.constant.DwdConstant;
import issac.mapper.MysqlDDL;
import lombok.SneakyThrows;

import java.util.Map;

public class MysqlUtil
{
    @SneakyThrows
    public static String getBaseDicLookUpDDL()
    {
        Map<String, String> configMap = ConfigurationUtil.parseProperty(ConfigConstant.PROPERTY_CONFIG_FILE_NAME);
        String driverName = configMap.get(ConfigConstant.MYSQL_DRIVER);
        String url = configMap.get(ConfigConstant.MYSQL_CONNECTION_URL);
        String userName = configMap.get(ConfigConstant.MYSQL_USER);
        String passWord = configMap.get(ConfigConstant.MYSQL_PASSWORD);
        String maxRows = configMap.get(ConfigConstant.FLINK_MYSQL_MAX_ROWS);
        String ttl = configMap.get(ConfigConstant.FLINK_MYSQL_TTL);
        
        MysqlDDL mysql = new MysqlDDL();
        String tableName = DwdConstant.DWD_TABLE_BASE_DIC;
        
        return mysql.createBaseDic(driverName, url, userName, passWord, tableName, maxRows, ttl);
    }
}