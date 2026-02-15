package zerodowntime.repository;

import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.SqlQuery;

import zerodowntime.dto.web.MessageDto;

import java.util.List;

public interface MessageRepository {
    @SqlQuery("SELECT m.message_id, m.author_id, m.text, m.pub_date, m.flagged, u.username, u.email " +
            "FROM message m JOIN user u ON m.author_id = u.user_id " +
            "WHERE m.flagged = 0 AND (u.user_id = :userId OR " +
            "u.user_id IN (SELECT whom_id FROM follower WHERE who_id = :userId)) " +
            "ORDER BY m.pub_date DESC LIMIT :limit")
    @RegisterBeanMapper(MessageDto.class)
    List<MessageDto> getUserTimelineMessages(
            @Bind("userId") int userId,
            @Bind("limit") int limit);

    @SqlQuery("SELECT m.message_id, m.author_id, m.text, m.pub_date, m.flagged, u.username, u.email " +
            "FROM message m JOIN user u ON m.author_id = u.user_id " +
            "WHERE m.flagged = 0 " +
            "ORDER BY m.pub_date DESC LIMIT :limit")
    @RegisterBeanMapper(MessageDto.class)
    List<MessageDto> getPublicTimelineMessages(@Bind("limit") int limit);

}