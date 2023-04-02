package my.service.controller;


import my.service.dao.FeedDAO;
import my.service.dao.FollowerDAO;
import my.service.dao.TweetDAO;
import my.service.dto.FollowerDTO;
import my.service.dto.TweetDTO;
import my.service.dto.FeedDTO;
import my.service.responseentity.BasicResponseEntity;
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
        return new BasicResponseEntity("success");
    }

    @RequestMapping(path = "/feed/{userID}", method = RequestMethod.GET)
    public FeedDTO getFeed(@PathVariable String userID) {
        FeedDTO feedDTO = null;

        try {
            FeedDAO feedDAO = new FeedDAO();
            feedDTO = feedDAO.getFeed(userID);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return feedDTO;
    }

    @RequestMapping(path = "/followers/{userID}", method = RequestMethod.GET)
    public FollowerDTO getFollowers(@PathVariable String userID) {
        FollowerDTO followerDTO = null;

        try {
            FollowerDAO followerDAO = new FollowerDAO();
            followerDTO = followerDAO.getFollowers(userID);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return followerDTO;
    }
}