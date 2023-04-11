package my.service.dao;

import my.service.dto.FeedDTO;
import my.service.dto.TweetDTO;
import my.service.repository.FeedRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class FeedDAO {
    private final FeedRepository feedRepository;


    @Autowired
    public FeedDAO(FeedRepository feedRepository) {
        this.feedRepository = feedRepository;
    }

    public FeedDTO getFeed(String userID) {
        Optional<FeedDTO> feed = feedRepository.findById(userID);

        if (feed.isPresent()) {
            return feed.get();
        } else {
            FeedDTO newFeed = new FeedDTO();
            newFeed.setUserID(userID);
            newFeed.setTweetsList(new ArrayList<>());
            return newFeed;
        }
    }

    public void addTweetToFeed(String userID, TweetDTO newTweet) {
        FeedDTO feed = getFeed(userID);
        feed.getTweetsList().add(0, newTweet);
        feedRepository.save(feed);
    }
}