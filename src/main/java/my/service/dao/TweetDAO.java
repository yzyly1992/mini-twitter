package my.service.dao;

import my.service.dto.FollowerDTO;
import my.service.dto.TweetDTO;
import my.service.repository.TweetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TweetDAO {

    private final TweetRepository tweetRepository;

    private final FeedDAO feedDAO;

    private final FollowerDAO followerDAO;

    @Autowired
    public TweetDAO(TweetRepository tweetRepository, FeedDAO feedDAO, FollowerDAO followerDAO) {
        this.tweetRepository = tweetRepository;
        this.feedDAO = feedDAO;
        this.followerDAO = followerDAO;
    }

    public void postTweet(TweetDTO tweetDTO) throws Exception {
        tweetRepository.save(tweetDTO);

        String userID = tweetDTO.getUserID();
        FollowerDTO followers = followerDAO.getFollowers(userID);

        // RedisDAO redisDAO = new RedisDAO();
        for (String follower : followers.getFollowersList()) {
            feedDAO.addTweetToFeed(follower, tweetDTO);

            // Also, populate this tweet to redis
            // redisDAO.addTweetToFeed(follower, tweetDTO);
        }
    }
}
