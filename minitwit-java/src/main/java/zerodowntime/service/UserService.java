package zerodowntime.service;

import java.util.List;

import zerodowntime.constants.AppConstants;
import zerodowntime.dto.web.MessageDto;
import zerodowntime.dto.web.MessageView;
import zerodowntime.dto.web.UserProfileData;
import zerodowntime.mapper.MessageMapper;
import zerodowntime.model.User;
import zerodowntime.repository.FollowerRepository;
import zerodowntime.repository.MessageRepository;
import zerodowntime.repository.UserRepository;

public class UserService {
    private UserRepository userRepository;
    private FollowerRepository followerRepository;
    private MessageRepository messageRepository;

    public UserService(UserRepository userRepository, FollowerRepository followerRepository,
            MessageRepository messageRepository) {
        this.userRepository = userRepository;
        this.followerRepository = followerRepository;
        this.messageRepository = messageRepository;
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

    public UserProfileData getProfileData(User profileUser, Integer currentUserId) {
        List<MessageDto> rawMessages = messageRepository.getMessagesByUserId(profileUser.getUserId(),
                AppConstants.PER_PAGE);

        List<MessageView> messages = rawMessages.stream()
                .map(MessageMapper::toView)
                .toList();

        boolean isFollowing = false;
        if (currentUserId != null) {
            isFollowing = followerRepository.isFollowing(currentUserId, profileUser.getUserId());
        }

        return new UserProfileData(messages, isFollowing);
    }
}
