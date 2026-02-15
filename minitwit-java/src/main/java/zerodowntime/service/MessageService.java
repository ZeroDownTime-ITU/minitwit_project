package zerodowntime.service;

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

    public MessageView createMessage(int authorId, String text) {
        long now = System.currentTimeMillis() / 1000;

        messageRepository.createMessage(authorId, text, now);

        // 2. Fetch User details to build the View DTO
        User user = userRepository.findById(authorId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + authorId));

        return new MessageView(
                user.getUsername(),
                text,
                FormatUtils.formatDatetime(now),
                FormatUtils.getGravatarUrl(user.getEmail(), 48),
                authorId);
    }
}
