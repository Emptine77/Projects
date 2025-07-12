CREATE TABLE movie_catalog.movies
(
    id                    SERIAL PRIMARY KEY,
    title                 VARCHAR(255)   NOT NULL UNIQUE,
    realise_date          DATE           NOT NULL,
    duration              Int            NOT NULL CHECK (duration > 0),
    description           TEXT           NOT NULL,
    age_limit             Int            NOT NULL,
    rating                SMALLINT       NOT NULL CHECK (rating >= 0 AND rating <= 10),
    country_of_production VARCHAR(255)   NOT NULL,
    director              VARCHAR(255)   NOT NULL,
    rental_period_start   DATE           NOT NULL,
    rental_period_end     DATE           NOT NULL CHECK (rental_period_end > rental_period_start),
    language              VARCHAR(255)   NOT NULL,
    base_price            DECIMAL(10, 2) NOT NULL CHECK (base_price > 0),
    poster_url            TEXT,
    trailer_url           TEXT
);