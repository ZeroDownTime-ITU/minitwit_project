package zerodowntime.repository;

import org.jooq.DSLContext;

public abstract class BaseRepository {
    protected final DSLContext db;

    protected BaseRepository(DSLContext db) {
        this.db = db;
    }
}