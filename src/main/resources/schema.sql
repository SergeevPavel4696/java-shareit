-- CREATE TABLE IF NOT EXISTS users (
--     id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
--     name VARCHAR(256),
--     email VARCHAR(512)
-- );
--
-- CREATE TABLE IF NOT EXISTS items (
--     id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
--     name VARCHAR(256),
--     description VARCHAR(512),
--     available BOOLEAN,
--     owner_id BIGINT
-- );
--
-- CREATE TABLE IF NOT EXISTS bookings (
--     id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
--     booking_start TIMESTAMP,
--     booking_end TIMESTAMP,
--     item_id INTEGER REFERENCES items(id),
--     booker_id INTEGER REFERENCES users(id),
--     status VARCHAR
-- );
--
-- CREATE TABLE IF NOT EXISTS comments (
--     id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
--     text VARCHAR(1000),
--     item_id BIGINT REFERENCES items(id),
--     author_id BIGINT REFERENCES users(id)
-- );



CREATE TABLE IF NOT EXISTS users (
    id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR UNIQUE NOT NULL,
    email VARCHAR UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS items (
    id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR NOT NULL,
    description VARCHAR NOT NULL,
    available BOOLEAN NOT NULL,
    owner_id INTEGER REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS bookings (
    id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    booking_start TIMESTAMP NOT NULL,
    booking_end TIMESTAMP NOT NULL,
    item_id INTEGER REFERENCES items(id) ON DELETE CASCADE,
    booker_id INTEGER REFERENCES users(id) ON DELETE CASCADE,
    status VARCHAR NOT NULL
);

CREATE TABLE IF NOT EXISTS comments (
    id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    text VARCHAR NOT NULL,
    item_id INTEGER REFERENCES items(id) ON DELETE CASCADE,
    author_id INTEGER REFERENCES users(id) ON DELETE CASCADE
);
