package zerodowntime.repository;

import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import zerodowntime.model.User;

import java.util.Optional;

@RegisterBeanMapper(User.class)
public interface UserRepository {

    @SqlQuery("SELECT * FROM users WHERE user_id = :userId")
    Optional<User> findById(@Bind("userId") Integer userId);

    @SqlQuery("SELECT * FROM users WHERE username = :username")
    Optional<User> findByUsername(@Bind("username") String username);

    @SqlUpdate("INSERT INTO users (username, email, pw_hash) VALUES (:username, :email, :pwHash)")
    void createUser(
            @Bind("username") String username,
            @Bind("email") String email,
            @Bind("pwHash") String pwHash);

    @SqlQuery("SELECT user_id FROM users WHERE username = :username")
    Optional<Integer> getUserIdByUsername(@Bind("username") String username);
}