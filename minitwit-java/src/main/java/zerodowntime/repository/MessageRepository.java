package zerodowntime.repository;

import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import zerodowntime.dto.web.MessageDto;
import java.util.List;

public interface MessageRepository {

    @SqlUpdate("INSERT INTO message (author_id, text, pub_date, flagged) VALUES (:authorId, :text, :pubDate, 0)")
    @GetGeneratedKeys
    int createMessage(
            @Bind("authorId") int authorId,
            @Bind("text") String text,
            @Bind("pubDate") long pubDate);

    @SqlQuery("SELECT m.message_id, m.author_id, m.text, m.pub_date, m.flagged, u.username, u.email " +
            "FROM message m JOIN users u ON m.author_id = u.user_id " +
            "WHERE m.flagged = 0 AND (u.user_id = :userId OR " +
            "u.user_id IN (SELECT whom_id FROM follower WHERE who_id = :userId)) " +
            "ORDER BY m.pub_date DESC LIMIT :limit OFFSET :offset")
    @RegisterBeanMapper(MessageDto.class)
    List<MessageDto> getUserTimelineMessages(
            @Bind("userId") int userId,
            @Bind("limit") int limit,
            @Bind("offset") int offset);

    @SqlQuery("SELECT COUNT(*) FROM message m JOIN users u ON m.author_id = u.user_id " +
            "WHERE m.flagged = 0 AND (u.user_id = :userId OR " +
            "u.user_id IN (SELECT whom_id FROM follower WHERE who_id = :userId))")
    int getUserTimelineCount(@Bind("userId") int userId);

    @SqlQuery("SELECT m.message_id, m.author_id, m.text, m.pub_date, m.flagged, u.username, u.email " +
            "FROM message m JOIN users u ON m.author_id = u.user_id " +
            "WHERE m.flagged = 0 " +
            "ORDER BY m.pub_date DESC LIMIT :limit OFFSET :offset")
    @RegisterBeanMapper(MessageDto.class)
    List<MessageDto> getPublicTimelineMessages(
            @Bind("limit") int limit,
            @Bind("offset") int offset);

    @SqlQuery("SELECT COUNT(*) FROM message WHERE flagged = 0")
    int getPublicTimelineCount();

    @SqlQuery("SELECT m.*, u.username, u.email FROM message m " +
            "JOIN users u ON u.user_id = m.author_id " +
            "WHERE u.user_id = :userId " +
            "ORDER BY m.pub_date DESC LIMIT :limit OFFSET :offset")
    @RegisterBeanMapper(MessageDto.class)
    List<MessageDto> getMessagesByUserId(
            @Bind("userId") int userId,
            @Bind("limit") int limit,
            @Bind("offset") int offset);

    @SqlQuery("SELECT COUNT(*) FROM message WHERE flagged = 0 AND author_id = :userId")
    int getAllMessagesUserCount(@Bind("userId") int userId);
}