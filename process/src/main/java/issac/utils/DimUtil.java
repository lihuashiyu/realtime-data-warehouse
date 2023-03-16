package issac.utils;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import issac.constant.ConfigConstant;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@Slf4j
public class DimUtil
{
    public static JSONObject getDimInfo(Connection connection, String tableName, String key) throws InvocationTargetException, SQLException, InstantiationException, IllegalAccessException
    {
        // 先查询 Redis
        Jedis jedis = JedisUtil.getJedis();
        String redisKey = "DIM:" + tableName + ":" + key;
        String dimJsonStr = jedis.get(redisKey);
        
        if (dimJsonStr != null)
        {
            jedis.expire(redisKey, 24 * 60 * 60);            // 重置过期时间
            jedis.close();                                           // 归还连接
            return JSON.parseObject(dimJsonStr);                     // 返回维度数据
        }
        
        // 拼接 SQL 语句
        String querySql = "select * from " + ConfigConstant.HBASE_SCHEMA + "." + tableName + " where id='" + key + "'";
        log.info("querySql>>>" + querySql);
        
        // 查询数据
        List<JSONObject> queryList = JdbcUtil.queryList(connection, querySql, JSONObject.class, false);
        
        // 将从 Phoenix 查询到的数据写入 Redis
        JSONObject dimInfo = queryList.get(0);
        jedis.set(redisKey, dimInfo.toJSONString());
        
        jedis.expire(redisKey, 24 * 60 * 60);                // 设置过期时间
        jedis.close();                                               // 归还连接
        
        return dimInfo;                                              // 返回结果
    }
    
    
    public static void delDimInfo(String tableName, String key)
    {
        Jedis jedis = JedisUtil.getJedis();                          // 获取连接
        jedis.del("DIM:" + tableName + ":" + key);               // 删除数据
        jedis.close();                                               // 归还连接
    }
    
    
    public static void main(String[] args) throws Exception
    {
        DruidDataSource dataSource = DruidDSUtil.createDataSource();
        DruidPooledConnection connection = dataSource.getConnection();
        
        long start = System.currentTimeMillis();
        JSONObject dimInfo = getDimInfo(connection, "dim_base_trademark", "18");
        
        long end = System.currentTimeMillis();
        JSONObject dimInfo2 = getDimInfo(connection, "dim_base_trademark", "18");
        
        long end2 = System.currentTimeMillis();
        
        log.info("{}", dimInfo);
        log.info("{}", dimInfo2);
        
        log.info("{}", end - start);  // 159  127  120  127  121  122  119
        log.info("{}", end2 - end);   // 8  8  8  1  1  1  1  0  0.5
        
        connection.close();
    }
}
