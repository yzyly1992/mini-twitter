package my.service.dto;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverted;

import java.util.List;

@DynamoDBTable(tableName = "feeds")
public class FeedDTO {
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