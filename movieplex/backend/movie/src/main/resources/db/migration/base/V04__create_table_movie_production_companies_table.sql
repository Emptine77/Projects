CREATE TABLE movie_catalog.production_companies
(
    movie_production_id INT NOT NULL,
    production_company  VARCHAR(255),
    PRIMARY KEY (movie_production_id, production_company),
    FOREIGN KEY (movie_production_id) REFERENCES movie_catalog.movies (id)
);