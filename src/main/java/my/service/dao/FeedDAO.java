package my.service.dao;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedQueryList;
import my.service.dto.FeedDTO;
import my.service.dto.TweetDTO;
import org.apache.commons.pool2.impl.GenericObjectPool;

import java.util.ArrayList;

public class FeedDAO {
    private GenericObjectPool<AmazonDynamoDB> dbClientPool;
    public FeedDAO() {
        dbClientPool = DynamoClientPool.getInstance();
    }

    public FeedDTO getFeed(String userID) throws Exception {
        AmazonDynamoDB dbClient = dbClientPool.borrowObject();
        DynamoDBMapper mapper = new DynamoDBMapper(dbClient);

        FeedDTO queryDTO = new FeedDTO();
        queryDTO.setUserID(userID);

        DynamoDBQueryExpression<FeedDTO> query = new DynamoDBQueryExpression<FeedDTO>().withHashKeyValues(queryDTO);

        PaginatedQueryList<FeedDTO> results = mapper.query(FeedDTO.class, query);

        FeedDTO returnDTO = null;
        if (results.isEmpty()) {
            returnDTO = new FeedDTO();
            returnDTO.setUserID(userID);
            returnDTO.setTweetsList(new ArrayList<>());
        }
        else {
            returnDTO = results.get(0);
        }
        dbClientPool.returnObject(dbClient);
        return returnDTO;
    }

    public void addTweetToFeed(String userID, TweetDTO tweetDTO) throws Exception {
        AmazonDynamoDB dbClient = dbClientPool.borrowObject();
        DynamoDBMapper mapper = new DynamoDBMapper(dbClient);

        FeedDTO feedDTO = this.getFeed(userID);
        feedDTO.getTweetsList().add(tweetDTO);
        mapper.save(feedDTO);

        dbClientPool.returnObject(dbClient);
    }
}
