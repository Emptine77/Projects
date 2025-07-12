CREATE TABLE movie_catalog.movie_users_reviews
(
    id       SERIAL PRIMARY KEY,
    movie_id INTEGER  NOT NULL,
    user_id  UUID     NOT NULL,
    rating   SMALLINT NOT NULL CHECK (rating >= 0 and rating <= 10),
    comment  VARCHAR(255),
    FOREIGN KEY (movie_id) REFERENCES movie_catalog.movies (id)
);