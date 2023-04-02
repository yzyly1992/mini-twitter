package my.service.dao;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import my.service.dto.TweetDTO;
import org.apache.commons.pool2.impl.GenericObjectPool;

public class TweetDAO {
    private GenericObjectPool<AmazonDynamoDB> dbClientPool;
    public TweetDAO() {
        dbClientPool = DynamoClientPool.getInstance();
    }

    public void postTweet(TweetDTO tweetDTO) {
        try {
            AmazonDynamoDB dbClient = dbClientPool.borrowObject();
            DynamoDBMapper mapper = new DynamoDBMapper(dbClient);
            mapper.save(tweetDTO);
            dbClientPool.returnObject(dbClient);
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
