package zerodowntime.repository;

import org.jooq.DSLContext;
import zerodowntime.generated.jooq.tables.Users;
import static zerodowntime.generated.jooq.Tables.*;
import java.util.List;

public class FollowerRepository extends BaseRepository {

    public FollowerRepository(DSLContext db) {
        super(db);
    }

    public int followUser(int whoId, int whomId) {
        return db.mergeInto(FOLLOWER)
                .using(db.selectOne())
                .on(FOLLOWER.WHO_ID.eq(whoId).and(FOLLOWER.WHOM_ID.eq(whomId)))
                .whenNotMatchedThenInsert(FOLLOWER.WHO_ID, FOLLOWER.WHOM_ID)
                .values(whoId, whomId)
                .execute();
    }

    public int unfollowUser(int whoId, int whomId) {
        return db.deleteFrom(FOLLOWER)
                .where(FOLLOWER.WHO_ID.eq(whoId))
                .and(FOLLOWER.WHOM_ID.eq(whomId))
                .execute();
    }

    public boolean isFollowing(Integer userId, Integer whomId) {
        return db.fetchExists(
                db.selectOne()
                        .from(FOLLOWER)
                        .where(FOLLOWER.WHO_ID.eq(userId))
                        .and(FOLLOWER.WHOM_ID.eq(whomId)));
    }

    public List<String> getUserFollowing(String username, int limit) {
        Users uWhom = USERS.as("u_whom");

        return db.select(uWhom.USERNAME)
                .from(USERS)
                .join(FOLLOWER).on(FOLLOWER.WHO_ID.eq(USERS.USER_ID))
                .join(uWhom).on(FOLLOWER.WHOM_ID.eq(uWhom.USER_ID))
                .where(USERS.USERNAME.eq(username))
                .limit(limit)
                .fetch(uWhom.USERNAME);
    }
}