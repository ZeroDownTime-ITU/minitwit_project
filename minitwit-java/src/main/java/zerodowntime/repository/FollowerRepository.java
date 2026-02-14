package zerodowntime.repository;

import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import java.util.List;
import java.util.Optional;

public interface FollowerRepository {

    @SqlUpdate("INSERT INTO follower (who_id, whom_id) VALUES (:userId, :whomId)")
    void followUser(
            @Bind("userId") Integer userId,
            @Bind("whomId") Integer whomId);

    @SqlUpdate("DELETE FROM follower WHERE who_id = :userId AND whom_id = :whomId")
    void unfollowUser(
            @Bind("userId") Integer userId,
            @Bind("whomId") Integer whomId);

    @SqlQuery("SELECT 1 FROM follower WHERE who_id = :userId AND whom_id = :whomId")
    Optional<Integer> isFollowing(
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