package issac.utils;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Slf4j
public class JedisUtil
{
    private static JedisPool jedisPool;
    
    
    private static void initJedisPool()
    {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(100);
        poolConfig.setMaxIdle(5);
        poolConfig.setMinIdle(5);
        poolConfig.setBlockWhenExhausted(true);
        poolConfig.setMaxWaitMillis(2000);
        poolConfig.setTestOnBorrow(true);
        jedisPool = new JedisPool(poolConfig, "hadoop102", 6379, 10000);
    }
    
    
    public static Jedis getJedis()
    {
        if (jedisPool == null) { initJedisPool(); }
        
        return jedisPool.getResource();                              // 获取 Jedis 客户端
    }
}
