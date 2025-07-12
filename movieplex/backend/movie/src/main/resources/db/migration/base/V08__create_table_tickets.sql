CREATE TABLE movie_catalog.tickets
(
    id                    SERIAL PRIMARY KEY,
    user_id               UUID         NOT NULL,
    status                VARCHAR(128) NOT NULL,
    purchase_time         TIMESTAMP    NOT NULL,
    seat_row              INTEGER      NOT NULL,
    seat_column           INTEGER      NOT NULL,
    price                 DECIMAL      NOT NULL CHECK (price > 0),
    movie_schedule_id     INTEGER      NOT NULL,
    ticket_refund_code_id INTEGER,
    FOREIGN KEY (movie_schedule_id) REFERENCES movie_catalog.movie_schedule (id)
);