package zerodowntime.service;

import zerodowntime.dto.web.MessageDto;
import zerodowntime.dto.web.MessageView;
import zerodowntime.mapper.MessageMapper;
import zerodowntime.repository.MessageRepository;

import java.util.List;
import java.util.stream.Collectors;

public class TimelineService {
    private final MessageRepository messageRepository;

    public TimelineService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public List<MessageView> getTimelineForUser(int userId, int limit, int offset) {
        List<MessageDto> rawMessages = messageRepository.getUserTimelineMessages(userId, limit, offset);

        return rawMessages.stream()
                .map(MessageMapper::toView)
                .collect(Collectors.toList());
    }

    public List<MessageView> getPublicTimeline(int limit, int offset) {
        List<MessageDto> rawMessages = messageRepository.getPublicTimelineMessagesPaged(limit, offset);

        return rawMessages.stream()
                .map(MessageMapper::toView)
                .collect(Collectors.toList());
    }

    public int getUserTimelineCount(int userId) {
        return messageRepository.getUserTimelineCount(userId);
    }

    public int getPublicTimelineCount() {
        return messageRepository.getPublicTimelineCount();
    }
}