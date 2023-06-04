CREATE TABLE IF NOT EXISTS users
(
    id    BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name  VARCHAR(255)                            NOT NULL,
    email VARCHAR(512)                            NOT NULL,
    CONSTRAINT pk_user PRIMARY KEY (id),
    CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS items
(
    id          BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name        VARCHAR(255)                            NOT NULL,
    description VARCHAR(1000)                           NOT NULL,
    available   BOOLEAN                                 NOT NULL,
    owner_id    BIGINT                                  NOT NULL,
    request_id  BIGINT,
    CONSTRAINT pk_item PRIMARY KEY (id),
    CONSTRAINT fk_owner FOREIGN KEY (owner_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS bookings
(
    id         BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    start_date TIMESTAMP WITHOUT TIME ZONE,
    end_date   TIMESTAMP WITHOUT TIME ZONE,
    item_id    BIGINT                                  NOT NULL,
    booker_id    BIGINT                                  NOT NULL,
    status     VARCHAR(200)                            NOT NULL,
    CONSTRAINT pk_booking PRIMARY KEY (id),
    CONSTRAINT fk_item FOREIGN KEY (item_id) REFERENCES items (id),
    CONSTRAINT fk_user FOREIGN KEY (booker_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS requests
(
    id          BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    description VARCHAR(1000)                           NOT NULL,
    requester_id     BIGINT                                  NOT NULL,
    created     TIMESTAMP WITHOUT TIME ZONE             NOT NULL,
    CONSTRAINT pk_request PRIMARY KEY (id),
    CONSTRAINT fk_request_user FOREIGN KEY (requester_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS comments
(
    id           BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    text VARCHAR(1000)                           NOT NULL,
    item_id      BIGINT                                  NOT NULL,
    author_id      BIGINT                                  NOT NULL,
    created      TIMESTAMP WITHOUT TIME ZONE             NOT NULL,
    CONSTRAINT pk_comment PRIMARY KEY (id),
    CONSTRAINT fk_item_id FOREIGN KEY (item_id) REFERENCES items (id),
    CONSTRAINT fk_user_id FOREIGN KEY (author_id) REFERENCES users (id)
);