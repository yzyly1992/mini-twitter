package my.service.dao;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisConnectionFactory {
    private static JedisPool jedisPool;

    public static JedisPool getJedisPool() {
        if (jedisPool == null) {
            JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
            // String redisHost = "feeds.xm6d3f.ng.0001.usw2.cache.amazonaws.com";
            String redisHost = "localhost";
            int redisPort = 6379;
            jedisPool = new JedisPool(jedisPoolConfig, redisHost, redisPort);
        }
        return jedisPool;
    }
}