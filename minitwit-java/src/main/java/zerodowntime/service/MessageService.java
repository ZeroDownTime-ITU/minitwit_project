package zerodowntime.service;

import java.util.List;
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

    public List<zerodowntime.dto.simulator.Message> getRecentMessages(int limit) {
    return messageRepository.getPublicTimelineMessages(limit)
            .stream()
            .map(m -> new zerodowntime.dto.simulator.Message(
                    m.getText(),
                    FormatUtils.formatDatetime(m.getPubDate()),
                    m.getUsername()))
            .toList();
    }

    public List<zerodowntime.dto.simulator.Message> getMessagesForUser(String username, int limit) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return messageRepository.getMessagesByUserId(user.getUserId(), limit)
                .stream()
                .map(m -> new zerodowntime.dto.simulator.Message(
                        m.getText(),
                        FormatUtils.formatDatetime(m.getPubDate()),
                        m.getUsername()))
                .toList();
    }
}