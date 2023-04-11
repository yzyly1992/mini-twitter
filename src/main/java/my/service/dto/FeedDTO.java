package my.service.dto;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import org.springframework.data.annotation.Id;

import java.util.List;

@DynamoDBTable(tableName = "feeds")
public class FeedDTO {
    @Id
    private String userID;
    private List<TweetDTO> tweetsList;

    @DynamoDBHashKey(attributeName = "userID")
    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    @DynamoDBAttribute(attributeName = "tweetsList")
    public List<TweetDTO> getTweetsList() {
        return tweetsList;
    }

    public void setTweetsList(List<TweetDTO> tweetsList) {
        this.tweetsList = tweetsList;
    }
}