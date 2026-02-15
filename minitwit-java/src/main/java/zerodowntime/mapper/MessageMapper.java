package zerodowntime.mapper;

import zerodowntime.dto.web.MessageDto;
import zerodowntime.dto.web.MessageView;
import zerodowntime.util.FormatUtils;

public class MessageMapper {
    public static MessageView toView(MessageDto dto) {
        return new MessageView(
                dto.getUsername(),
                dto.getText(),
                FormatUtils.formatDatetime(dto.getPubDate()),
                FormatUtils.getGravatarUrl(dto.getEmail(), 48),
                dto.getAuthorId());
    }
}
