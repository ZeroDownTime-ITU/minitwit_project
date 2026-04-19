package zerodowntime.repository;

import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import zerodowntime.dto.web.MessageDto;
import static zerodowntime.generated.jooq.Tables.*;

import java.util.List;

public class MessageRepository extends BaseRepository {

    public MessageRepository(DSLContext db) {
        super(db);
    }

    public int createMessage(Integer authorId, String text, long pubDate) {
        var message = db.newRecord(MESSAGE);

        message.setAuthorId(authorId);
        message.setText(text);
        message.setPubDate(pubDate);
        message.setFlagged(0);

        message.store();

        return message.getMessageId();
    }

    public List<MessageDto> getUserTimelineMessages(int userId, int limit, int offset) {
        var ownMessages = db.select(MESSAGE.fields())
                .select(USERS.USERNAME, USERS.EMAIL)
                .from(MESSAGE)
                .join(USERS).on(MESSAGE.AUTHOR_ID.eq(USERS.USER_ID))
                .where(MESSAGE.AUTHOR_ID.eq(userId))
                .and(MESSAGE.FLAGGED.eq(0));

        var followedMessages = db.select(MESSAGE.fields())
                .select(USERS.USERNAME, USERS.EMAIL)
                .from(MESSAGE)
                .join(USERS).on(MESSAGE.AUTHOR_ID.eq(USERS.USER_ID))
                .where(MESSAGE.AUTHOR_ID.in(
                        db.select(FOLLOWER.WHOM_ID)
                                .from(FOLLOWER)
                                .where(FOLLOWER.WHO_ID.eq(userId))))
                .and(MESSAGE.FLAGGED.eq(0));

        return db.select()
                .from(ownMessages.unionAll(followedMessages))
                .orderBy(DSL.field("pub_date").desc())
                .limit(limit)
                .offset(offset)
                .fetchInto(MessageDto.class);
    }

    public int getUserTimelineCount(int userId) {
        int own = db.selectCount()
                .from(MESSAGE)
                .where(MESSAGE.AUTHOR_ID.eq(userId))
                .and(MESSAGE.FLAGGED.eq(0))
                .fetchOne(0, int.class);

        int followed = db.selectCount()
                .from(MESSAGE)
                .where(MESSAGE.AUTHOR_ID.in(
                        db.select(FOLLOWER.WHOM_ID)
                                .from(FOLLOWER)
                                .where(FOLLOWER.WHO_ID.eq(userId))))
                .and(MESSAGE.FLAGGED.eq(0))
                .fetchOne(0, int.class);

        return own + followed;
    }

    public List<MessageDto> getPublicTimelineMessages(int limit, int offset) {
        return db.select(MESSAGE.fields())
                .select(USERS.USERNAME, USERS.EMAIL)
                .from(MESSAGE)
                .join(USERS).on(MESSAGE.AUTHOR_ID.eq(USERS.USER_ID))
                .where(MESSAGE.FLAGGED.eq(0))
                .orderBy(MESSAGE.PUB_DATE.desc())
                .limit(limit)
                .offset(offset)
                .fetchInto(MessageDto.class);
    }

    public int getPublicTimelineCount() {
        try {
            return db.resultQuery("SELECT reltuples::bigint FROM pg_class WHERE relname = 'message'")
                    .fetchOne()
                    .get(0, Integer.class);
        } catch (Exception e) { // this is for the test H2 database. It doesn't have the pg_class, so needs this
                                // fix.
            return db.selectCount()
                    .from(MESSAGE)
                    .where(MESSAGE.FLAGGED.eq(0))
                    .fetchOne(0, int.class);
        }
    }

    public List<MessageDto> getMessagesByUserId(int userId, int limit, int offset) {
        return db.select(MESSAGE.fields())
                .select(USERS.USERNAME, USERS.EMAIL)
                .from(MESSAGE)
                .join(USERS).on(MESSAGE.AUTHOR_ID.eq(USERS.USER_ID))
                .where(USERS.USER_ID.eq(userId))
                .orderBy(MESSAGE.PUB_DATE.desc())
                .limit(limit)
                .offset(offset)
                .fetchInto(MessageDto.class);
    }

    public int getAllMessagesUserCount(int userId) {
        return db.selectCount()
                .from(MESSAGE)
                .where(MESSAGE.FLAGGED.eq(0))
                .and(MESSAGE.AUTHOR_ID.eq(userId))
                .fetchOne(0, int.class);
    }
}