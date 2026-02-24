package zerodowntime.service;

import java.util.List;

import zerodowntime.dto.simulator.Message;
import zerodowntime.dto.web.MessageDto;
import zerodowntime.dto.web.MessageView;
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

    public MessageView addMessage(int authorId, String text) {
        long currentTime = System.currentTimeMillis() / 1000;
        messageRepository.createMessage(authorId, text, currentTime);

        User user = userRepository.findById(authorId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + authorId));

        return new MessageView(
                user.getUsername(),
                text,
                FormatUtils.formatDatetime(currentTime),
                FormatUtils.getGravatarUrl(user.getEmail(), 48),
                authorId);
    }

    public List<Message> getRecentMessages(int limit) {
        List<MessageDto> rawMessages = messageRepository.getPublicTimelineMessages(limit);

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

        List<MessageDto> rawMessages = messageRepository.getMessagesByUserId(user.getUserId(), limit);

        return rawMessages.stream()
                .map(m -> new Message(
                        m.getText(),
                        FormatUtils.formatDatetime(m.getPubDate()),
                        m.getUsername()))
                .toList();
    }
}