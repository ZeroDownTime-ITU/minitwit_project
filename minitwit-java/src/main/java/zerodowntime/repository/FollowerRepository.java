package zerodowntime.repository;

import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
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
}