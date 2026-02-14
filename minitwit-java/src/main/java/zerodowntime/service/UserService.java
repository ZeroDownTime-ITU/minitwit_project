package zerodowntime.service;

import java.util.List;
import zerodowntime.model.User;
import zerodowntime.repository.FollowerRepository;
import zerodowntime.repository.UserRepository;

public class UserService {
    private UserRepository userRepository;
    private FollowerRepository followerRepository;

    public UserService(UserRepository userRepository, FollowerRepository followerRepository) {
        this.userRepository = userRepository;
        this.followerRepository = followerRepository;
    }

    public Integer getUserIdByUsername(String username) {
        return userRepository.findByUsername(username).map(User::getUserId).orElse(null);
    }

    public void followUser(Integer userId, Integer userToFollowId) {
        followerRepository.followUser(userId, userToFollowId);
    }   

    public List<String> getUserFollowing(String username, int limit) {
        return followerRepository.getUserFollowing(username, limit);
    }
}
