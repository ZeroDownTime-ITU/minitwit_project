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

    public User getUserById(Integer userId) {
        return userRepository.findById(userId).orElse(null);
    }

    public Integer getUserIdByUsername(String username) {
        return userRepository.findByUsername(username).map(User::getUserId).orElse(null);
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    public boolean followUser(Integer userId, Integer userToFollowId) {
        return followerRepository.followUser(userId, userToFollowId) > 0;
    }

    public boolean unfollowUser(Integer userId, Integer userToUnfollowId) {
        return followerRepository.unfollowUser(userId, userToUnfollowId) > 0;
    }

    public List<String> getUserFollowing(String username, int limit) {
        return followerRepository.getUserFollowing(username, limit);
    }

    public boolean isUserFollowingProfile(Integer currentUserId, Integer profileUserId) {
        return followerRepository.isFollowing(currentUserId, profileUserId);
    }
}
