package my.service.dao;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class RedisDAO {
    private final JedisPool jedisPool;

    public RedisDAO() {
        this.jedisPool = RedisConnectionFactory.getJedisPool();
    }

    public String getKey(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.get(key);
        }
    }

    public void setKey(String key, String value) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.set(key, value);
        }
    }
}
