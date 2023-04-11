package my.service.controller;


import my.service.dao.FeedDAO;
import my.service.dao.FollowerDAO;
import my.service.dao.TweetDAO;
import my.service.dto.FollowDTO;
import my.service.dto.FollowerDTO;
import my.service.dto.TweetDTO;
import my.service.dto.FeedDTO;
import my.service.responseentity.BasicResponseEntity;
import my.service.responseentity.FeedResponseEntity;
import my.service.responseentity.FollowerResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;


@RestController
@EnableWebMvc
public class MiniTwitterController {

    private final TweetDAO tweetDAO;

    private final FeedDAO feedDAO;

    private final FollowerDAO followerDAO;

    @Autowired
    public MiniTwitterController(TweetDAO tweetDAO, FeedDAO feedDAO, FollowerDAO followerDAO) {
        this.tweetDAO = tweetDAO;
        this.feedDAO = feedDAO;
        this.followerDAO = followerDAO;
    }

    @RequestMapping(path = "/tweet", method = RequestMethod.POST)
    public BasicResponseEntity postTweet(@RequestBody TweetDTO tweet) {
        tweet.setTweetID(UUID.randomUUID().toString());
        LocalDateTime currentDateTime = LocalDateTime.now();
        String timestamp = currentDateTime.format(DateTimeFormatter.ISO_DATE_TIME);
        tweet.setTimestamp(timestamp);

        try {
            tweetDAO.postTweet(tweet);
        } catch (Exception e) {
            return new BasicResponseEntity(e.getMessage());
        }
        return new BasicResponseEntity();
    }

    @RequestMapping(path = "/feed/{userID}", method = RequestMethod.GET)
    public FeedResponseEntity getFeed(@PathVariable String userID) {
        FeedResponseEntity res = new FeedResponseEntity();

        try {
            // These lines are getting feeds from DynamoDB
            //  FeedDAO feedDAO = new FeedDAO();
             FeedDTO feedDTO = feedDAO.getFeed(userID);

            // These lines are getting feeds from Redis
            // RedisDAO redisDAO = new RedisDAO();
            // FeedDTO feedDTO = redisDAO.getFeed(userID);
            res.setData(feedDTO);
        } catch (Exception e) {
            res.setMessage(e.getMessage());
        }
        return res;
    }

    @RequestMapping(path = "/followers/{userID}", method = RequestMethod.GET)
    public FollowerResponseEntity getFollowers(@PathVariable String userID) {
        FollowerResponseEntity res = new FollowerResponseEntity();

        try {
            FollowerDTO followerDTO = followerDAO.getFollowers(userID);
            res.setData(followerDTO);
        } catch (Exception e) {
            res.setMessage(e.getMessage());
        }
        return res;
    }


    @RequestMapping(path = "/follow", method = RequestMethod.POST)
    public BasicResponseEntity follow(@RequestBody FollowDTO follow) {
        BasicResponseEntity res = new BasicResponseEntity();

        try {
            followerDAO.follow(follow.getFollower(), follow.getFollowee());
        } catch (Exception e) {
            res.setMessage(e.getMessage());
        }
        return res;
    }
}