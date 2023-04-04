package my.service.dao;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedQueryList;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.UpdateItemRequest;

import my.service.dto.FeedDTO;
import my.service.dto.TweetDTO;
import org.apache.commons.pool2.impl.GenericObjectPool;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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

    public void addTweetToFeed(String userID, TweetDTO newTweet) throws Exception {
        AmazonDynamoDB dbClient = dbClientPool.borrowObject();
        DynamoDBMapper mapper = new DynamoDBMapper(dbClient);

        // The new way just appends the new tweet to the front of the tweetsList
        Map<String, AttributeValue> newTweetMap = new HashMap<>();

        newTweetMap.put("userID", new AttributeValue(newTweet.getUserID()));
        newTweetMap.put("content", new AttributeValue(newTweet.getContent()));
        newTweetMap.put("timestamp", new AttributeValue(newTweet.getTimestamp()));
        newTweetMap.put("tweetID", new AttributeValue(newTweet.getTweetID()));

        UpdateItemRequest updateItemRequest = new UpdateItemRequest()
                .withTableName("feeds")
                .withKey(
                        Collections.singletonMap("userID", new AttributeValue(userID))
                )
                .withUpdateExpression("SET tweetsList = list_append(:newTweet, if_not_exists(tweetsList, :emptyList))")
                .withExpressionAttributeValues(new HashMap<String, AttributeValue>() {{
                                                   put(":newTweet", new AttributeValue().withL(new AttributeValue().withM(newTweetMap)));
                                                   put(":emptyList", new AttributeValue().withL(Collections.emptyList()));
                                               }}
                );

        dbClient.updateItem(updateItemRequest);
        dbClientPool.returnObject(dbClient);
    }
}
