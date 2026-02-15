package zerodowntime.repository;

import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import java.util.List;

public interface FollowerRepository {

    @SqlUpdate("INSERT OR IGNORE INTO follower (who_id, whom_id) VALUES (:whoId, :whomId)")
    int followUser(
            @Bind("whoId") int whoId,
            @Bind("whomId") int whomId);

    @SqlUpdate("DELETE FROM follower WHERE who_id = :whoId AND whom_id = :whomId")
    int unfollowUser(
            @Bind("whoId") int whoId,
            @Bind("whomId") int whomId);

    @SqlQuery("SELECT EXISTS(SELECT 1 FROM follower WHERE who_id = :userId AND whom_id = :whomId)")
    boolean isFollowing(
            @Bind("userId") Integer userId,
            @Bind("whomId") Integer whomId);

    @SqlQuery("""
                SELECT u_whom.username
                FROM user u_who
                JOIN follower f ON f.who_id = u_who.user_id
                JOIN user u_whom ON f.whom_id = u_whom.user_id
                WHERE u_who.username = :username
                LIMIT :limit
            """)
    List<String> getUserFollowing(
            @Bind("username") String username,
            @Bind("limit") int limit);
}