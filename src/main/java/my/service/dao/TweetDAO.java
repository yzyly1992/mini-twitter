package my.service.dao;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import my.service.dto.FollowerDTO;
import my.service.dto.TweetDTO;
import org.apache.commons.pool2.impl.GenericObjectPool;

public class TweetDAO {
    private GenericObjectPool<AmazonDynamoDB> dbClientPool;
    public TweetDAO() {
        dbClientPool = DynamoClientPool.getInstance();
    }

    public void postTweet(TweetDTO tweetDTO) throws Exception {
        AmazonDynamoDB dbClient = dbClientPool.borrowObject();
        DynamoDBMapper mapper = new DynamoDBMapper(dbClient);
        mapper.save(tweetDTO);
        dbClientPool.returnObject(dbClient);

        String userID = tweetDTO.getUserID();
        FollowerDAO followerDAO = new FollowerDAO();
        FollowerDTO followers = followerDAO.getFollowers(userID);

        FeedDAO feedDAO = new FeedDAO();
        RedisDAO redisDAO = new RedisDAO();
        for (String follower : followers.getFollowersList()) {
            feedDAO.addTweetToFeed(follower, tweetDTO);

            // Also, populate this tweet to redis
            redisDAO.addTweetToFeed(follower, tweetDTO);
        }
    }
}
