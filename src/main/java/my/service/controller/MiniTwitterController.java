package my.service.controller;


import my.service.dao.FeedDAO;
import my.service.dao.FollowerDAO;
import my.service.dao.TweetDAO;
import my.service.dto.FollowerDTO;
import my.service.dto.TweetDTO;
import my.service.dto.FeedDTO;
import my.service.responseentity.BasicResponseEntity;
import my.service.responseentity.FeedResponseEntity;
import my.service.responseentity.FollowerResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;


@RestController
@EnableWebMvc
public class MiniTwitterController {
    @RequestMapping(path = "/tweet", method = RequestMethod.POST)
    public BasicResponseEntity postTweet(@RequestBody TweetDTO tweet) {
        tweet.setTweetID(UUID.randomUUID().toString());
        LocalDateTime currentDateTime = LocalDateTime.now();
        String timestamp = currentDateTime.format(DateTimeFormatter.ISO_DATE_TIME);
        tweet.setTimestamp(timestamp);

        try {
            TweetDAO tweetDAO = new TweetDAO();
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
            FeedDAO feedDAO = new FeedDAO();
            FeedDTO feedDTO = feedDAO.getFeed(userID);
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
            FollowerDAO followerDAO = new FollowerDAO();
            FollowerDTO followerDTO = followerDAO.getFollowers(userID);
            res.setData(followerDTO);
        } catch (Exception e) {
            res.setMessage(e.getMessage());
        }
        return res;
    }
}