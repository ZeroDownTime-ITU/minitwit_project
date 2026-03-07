package zerodowntime.repository;

import org.jooq.DSLContext;
import static zerodowntime.generated.jooq.Tables.*;
import zerodowntime.generated.jooq.tables.records.UserRecord;
import java.util.Optional;

public class UserRepository extends BaseRepository {

    public UserRepository(DSLContext db) {
        super(db);
    }

    public Optional<UserRecord> findById(Integer userId) {
        return db.selectFrom(USERS)
                .where(USERS.USER_ID.eq(userId))
                .fetchOptional();
    }

    public Optional<UserRecord> findByUsername(String username) {
        return db.selectFrom(USERS)
                .where(USERS.USERNAME.eq(username))
                .fetchOptional();
    }

    public void createUser(String username, String email, String pwHash) {
        var user = db.newRecord(USERS);

        user.setUsername(username);
        user.setEmail(email);
        user.setPwHash(pwHash);

        user.store();
    }

    public Optional<Integer> getUserIdByUsername(String username) {
        return db.select(USERS.USER_ID)
                .from(USERS)
                .where(USERS.USERNAME.eq(username))
                .fetchOptional(USERS.USER_ID);
    }
}