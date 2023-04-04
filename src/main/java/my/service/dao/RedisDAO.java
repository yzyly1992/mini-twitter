package my.service.dao;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import my.service.dto.FeedDTO;
import my.service.dto.TweetDTO;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

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

    public void addTweetToFeed(String userID, TweetDTO tweet) {
        try (Jedis jedis = jedisPool.getResource()) {
            Gson gson = new Gson();
            String key = "feeds-" + userID;
            jedis.lpush(key, gson.toJson(tweet));
        }
    }

    public FeedDTO getFeed(String userID) throws Exception {
        try (Jedis jedis = jedisPool.getResource()) {
            Gson gson = new Gson();
            String key = "feeds-" + userID;
            List<String> tweetJsonList = jedis.lrange(key, 0, -1);
            Type listType = new TypeToken<List<TweetDTO>>() {}.getType();
            List<TweetDTO> tweets = gson.fromJson(tweetJsonList.toString(), listType);
            FeedDTO feed = new FeedDTO();
            feed.setUserID(userID);
            feed.setTweetsList(tweets != null ? tweets : new ArrayList<>());
            return feed;
        }
    }
}
