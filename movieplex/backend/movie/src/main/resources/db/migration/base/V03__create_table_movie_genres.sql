-- Table: geo_object_accessibility
-- Goals: To store attached accessibility values of Geo objects
-- Fields: geo_object_id, accessibility_type

CREATE TABLE movie_catalog.movie_genres
(
    movie_id INT NOT NULL,
    genres   VARCHAR(64),
    PRIMARY KEY (movie_id, genres),
    FOREIGN KEY (movie_id) REFERENCES movie_catalog.movies (id)
);