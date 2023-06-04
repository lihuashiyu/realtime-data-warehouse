package issac.utils;

import issac.constant.ConfigConstant;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.time.Duration;
import java.util.Map;

@Slf4j
public class JedisUtil
{
    private static JedisPool jedisPool;
    
    
    @SneakyThrows
    private static void initJedisPool()
    {
        Map<String, String> configMap = ConfigurationUtil.parseProperty(ConfigConstant.PROPERTY_CONFIG_FILE_NAME);
        log.debug("Application Configuration = {}", configMap);
        
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(Integer.parseInt(configMap.get(ConfigConstant.REDIS_MAX_TOTAL)));
        poolConfig.setMaxIdle(Integer.parseInt(configMap.get(ConfigConstant.REDIS_MAX_IDLE)));
        poolConfig.setMinIdle(Integer.parseInt(configMap.get(ConfigConstant.REDIS_MIN_IDLE)));
        poolConfig.setBlockWhenExhausted(Boolean.parseBoolean(configMap.get(ConfigConstant.REDIS_BLOCK_WHEN_EXHAUSTED)));
        poolConfig.setMaxWait(Duration.ofMillis(Integer.parseInt(configMap.get(ConfigConstant.REDIS_MAX_WAIT_MILLIS))));
        poolConfig.setTestOnBorrow(Boolean.parseBoolean(configMap.get(ConfigConstant.REDIS_TEST_ON_BORROW)));
        log.info("Redis Parameters = {}", poolConfig);
        
        String redisHost = configMap.get(ConfigConstant.REDIS_HOST);
        int redisPort = Integer.parseInt(configMap.get(ConfigConstant.REDIS_PORT));
        int redisTimeOut = Integer.parseInt(configMap.get(ConfigConstant.REDIS_TIME_OUT));
        
        jedisPool = new JedisPool(poolConfig, redisHost, redisPort, redisTimeOut);
    }
    
    
    public static Jedis getJedis()
    {
        if (ObjectUtils.isEmpty(jedisPool)) 
        {
            initJedisPool(); 
        }
        
        return jedisPool.getResource();                              // 获取 Jedis 客户端
    }
}
