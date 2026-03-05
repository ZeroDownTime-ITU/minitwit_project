package zerodowntime.service;

import java.util.List;

import zerodowntime.dto.simulator.Message;
import zerodowntime.dto.web.MessageDto;
import zerodowntime.model.User;
import zerodowntime.repository.MessageRepository;
import zerodowntime.repository.UserRepository;
import zerodowntime.util.FormatUtils;

public class MessageService {
    private MessageRepository messageRepository;
    private UserRepository userRepository;

    public MessageService(MessageRepository messageRepository, UserRepository userRepository) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
    }

    public void addMessage(int authorId, String text) {
        long currentTime = System.currentTimeMillis() / 1000;
        messageRepository.createMessage(authorId, text, currentTime);
    }

    public List<Message> getRecentMessages(int limit) {
        List<MessageDto> rawMessages = messageRepository.getPublicTimelineMessages(limit, 0);

        return rawMessages.stream()
                .map(m -> new Message(
                        m.getText(),
                        FormatUtils.formatDatetime(m.getPubDate()),
                        m.getUsername()))
                .toList();
    }

    public List<Message> getMessagesForUser(String username, int limit) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));

        List<MessageDto> rawMessages = messageRepository.getMessagesByUserId(user.getUserId(), limit, 0);

        return rawMessages.stream()
                .map(m -> new Message(
                        m.getText(),
                        FormatUtils.formatDatetime(m.getPubDate()),
                        m.getUsername()))
                .toList();
    }
}