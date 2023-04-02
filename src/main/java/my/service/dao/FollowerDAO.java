package my.service.dao;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedQueryList;
import my.service.dto.FeedDTO;
import my.service.dto.FollowerDTO;
import my.service.dto.TweetDTO;
import org.apache.commons.pool2.impl.GenericObjectPool;

import java.util.ArrayList;

public class FollowerDAO {

    private GenericObjectPool<AmazonDynamoDB> dbClientPool;
    public FollowerDAO() {
        dbClientPool = DynamoClientPool.getInstance();
    }

    public FollowerDTO getFollowers(String userID) throws Exception {
        AmazonDynamoDB dbClient = dbClientPool.borrowObject();
        DynamoDBMapper mapper = new DynamoDBMapper(dbClient);

        FollowerDTO queryDTO = new FollowerDTO();
        queryDTO.setUserID(userID);

        DynamoDBQueryExpression<FollowerDTO> query = new DynamoDBQueryExpression<FollowerDTO>().withHashKeyValues(queryDTO);

        PaginatedQueryList<FollowerDTO> results = mapper.query(FollowerDTO.class, query);

        FollowerDTO returnDTO = null;
        if (results.isEmpty()) {
            returnDTO = new FollowerDTO();
            returnDTO.setUserID(userID);
            returnDTO.setFollowersList(new ArrayList<>());
        }
        else {
            returnDTO = results.get(0);
        }
        dbClientPool.returnObject(dbClient);
        return returnDTO;
    }
}
