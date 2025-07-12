CREATE TABLE movie_catalog.movie_schedule
(
    id             SERIAL PRIMARY KEY,
    movie_id       INTEGER      NOT NULL,
    start_datetime TIMESTAMP    NOT NULL,
    movie_format   VARCHAR(128) NOT NULL,
    cinema_room_id INTEGER      NOT NULL,
    ticket_price   DECIMAL      NOT NULL CHECK (ticket_price > 0),
    FOREIGN KEY (movie_id) REFERENCES movie_catalog.movies (id),
    FOREIGN KEY (cinema_room_id) REFERENCES movie_catalog.cinema_rooms (id)
);