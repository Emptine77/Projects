CREATE TABLE movie_catalog.ticket_refund_codes
(
    refund_code     TEXT PRIMARY KEY,
    ticket_id       INTEGER NOT NULL,
    expiration_date DATE,
    is_used         BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (ticket_id) REFERENCES movie_catalog.tickets (id)
);