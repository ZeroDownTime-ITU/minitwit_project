CREATE TABLE IF NOT EXISTS users (
  user_id SERIAL PRIMARY KEY,
  username TEXT NOT NULL,
  email TEXT NOT NULL,
  pw_hash TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS follower (
  who_id INTEGER REFERENCES users(user_id),
  whom_id INTEGER REFERENCES users(user_id),
  PRIMARY KEY (who_id, whom_id)
);

CREATE TABLE IF NOT EXISTS message (
  message_id SERIAL PRIMARY KEY,
  author_id INTEGER NOT NULL REFERENCES users(user_id),
  text TEXT NOT NULL,
  pub_date BIGINT,
  flagged INTEGER DEFAULT 0
);