package my.service.dao;

import com.google.gson.Gson;
import my.service.dto.FeedDTO;
import my.service.dto.TweetDTO;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.ArrayList;

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
            String feedJson = jedis.get(userID);
            if (feedJson == null) {
                FeedDTO feedDTO = new FeedDTO();
                feedDTO.setUserID(userID);
                feedDTO.setTweetsList(new ArrayList<>());
                feedDTO.getTweetsList().add(tweet);
                String newFeedJson = gson.toJson(feedDTO);
                jedis.set(userID, newFeedJson);
            }
            else {
                FeedDTO oldDTO = gson.fromJson(feedJson, FeedDTO.class);
                oldDTO.getTweetsList().add(tweet);
                String newFeedJson = gson.toJson(oldDTO);
                jedis.set(userID, newFeedJson);
            }
        }
    }

    public FeedDTO getFeed(String userID) {
        try (Jedis jedis = jedisPool.getResource()) {
            Gson gson = new Gson();
            String feedJson = jedis.get(userID);
            if (feedJson == null) {
                FeedDTO emptyDTO = new FeedDTO();
                emptyDTO.setUserID(userID);
                emptyDTO.setTweetsList(new ArrayList<>());
                return emptyDTO;
            }
            else {
                return gson.fromJson(feedJson, FeedDTO.class);
            }
        }
    }
}
