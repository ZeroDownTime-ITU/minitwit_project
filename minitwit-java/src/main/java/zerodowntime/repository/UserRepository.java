package zerodowntime.repository;

import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import zerodowntime.model.User;

import java.util.Optional;

@RegisterBeanMapper(User.class)
public interface UserRepository {
    @SqlQuery("SELECT * FROM user WHERE username = :username")
    Optional<User> findByUsername(@Bind("username") String username);

    @SqlQuery("SELECT * FROM user WHERE user_id = :userId")
    Optional<User> findById(@Bind("userId") Integer userId);

    @SqlUpdate("INSERT INTO user (username, email, pw_hash) VALUES (:username, :email, :pwHash)")
    void createUser(
            @Bind("username") String username,
            @Bind("email") String email,
            @Bind("pwHash") String pwHash);

    @SqlQuery("SELECT user_id FROM user WHERE username = :username")
    Optional<Integer> getUserIdByUsername(@Bind("username") String username);
}