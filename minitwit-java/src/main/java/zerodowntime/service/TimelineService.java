package zerodowntime.service;

import zerodowntime.dto.web.MessageDto;
import zerodowntime.dto.web.MessageView;
import zerodowntime.repository.MessageRepository;
import zerodowntime.util.FormatUtils;

import java.util.List;
import java.util.stream.Collectors;

public class TimelineService {
    private final MessageRepository messageRepo;

    public TimelineService(MessageRepository messageRepo) {
        this.messageRepo = messageRepo;
    }

    public List<MessageView> getTimelineForUser(int userId, int limit) {
        List<MessageDto> rawMessages = messageRepo.getUserTimelineMessages(userId, limit);

        return rawMessages.stream()
                .map(this::convertToView)
                .collect(Collectors.toList());
    }

    public List<MessageView> getPublicTimeline(int limit) {
        List<MessageDto> rawMessages = messageRepo.getPublicTimelineMessages(limit);

        return rawMessages.stream()
                .map(this::convertToView)
                .collect(Collectors.toList());
    }

    private MessageView convertToView(MessageDto dto) {
        return new MessageView(
                dto.getUsername(),
                dto.getText(),
                FormatUtils.formatDatetime(dto.getPubDate()),
                FormatUtils.gravatarUrl(dto.getEmail(), 48),
                dto.getAuthorId());
    }
}