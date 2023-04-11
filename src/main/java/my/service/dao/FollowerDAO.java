package my.service.dao;

import my.service.dto.FollowerDTO;
import my.service.repository.FollowerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class FollowerDAO {

    private final FollowerRepository followerRepository;

    @Autowired
    public FollowerDAO(FollowerRepository followerRepository) {
        this.followerRepository = followerRepository;
    }

    public FollowerDTO getFollowers(String userID) {
        Optional<FollowerDTO> followers = followerRepository.findById(userID);

        if (followers.isPresent()) {
            return followers.get();
        } else {
            FollowerDTO newFollowers = new FollowerDTO();
            newFollowers.setUserID(userID);
            newFollowers.setFollowersList(new ArrayList<>());
            return newFollowers;
        }
    }

    public void follow(String follower, String followee) {
        FollowerDTO followers = getFollowers(followee);
        if (!followers.getFollowersList().contains(follower)) {
            followers.getFollowersList().add(follower);
        }

        followerRepository.save(followers);
    }
}