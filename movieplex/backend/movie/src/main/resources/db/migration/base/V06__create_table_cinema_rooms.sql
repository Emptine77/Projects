CREATE TABLE movie_catalog.cinema_rooms
(
    id           SERIAL PRIMARY KEY,
    name         VARCHAR(255) NOT NULL,
    row_count    INTEGER      NOT NULL,
    column_count INTEGER      NOT NULL,
    room_type    VARCHAR(128) NOT NULL
);