package zerodowntime.service;

import zerodowntime.constants.AppConstants;
import zerodowntime.dto.web.MessageDto;
import zerodowntime.dto.web.MessageView;
import zerodowntime.mapper.MessageMapper;
import zerodowntime.repository.MessageRepository;

import java.util.List;

public class TimelineService {
    private final MessageRepository messageRepository;

    public TimelineService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public List<MessageView> getTimelineForUser(int userId, int limit, int offset) {
        List<MessageDto> rawMessages = messageRepository.getUserTimelineMessages(userId, limit, offset);

        return rawMessages.stream()
                .map(MessageMapper::toView)
                .toList();
    }

    public List<MessageView> getPublicTimeline(int limit, int offset) {
        List<MessageDto> rawMessages = messageRepository.getPublicTimelineMessages(limit, offset);

        return rawMessages.stream()
                .map(MessageMapper::toView)
                .toList();
    }

    public List<MessageView> getProfileMessages(Integer profileUserId, int offset) {
        List<MessageDto> rawMessages = messageRepository.getMessagesByUserId(profileUserId,
                AppConstants.PER_PAGE, offset);

        return rawMessages.stream()
                .map(MessageMapper::toView)
                .toList();
    }

    public int countUserTimelineMessages(int userId) {
        return messageRepository.getUserTimelineCount(userId);
    }

    public int countPublicTimelineMessages() {
        return messageRepository.getPublicTimelineCount();
    }

    public int countProfileMessages(int userId) {
        return messageRepository.getAllMessagesUserCount(userId);
    }
}