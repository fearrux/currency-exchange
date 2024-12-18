CREATE TABLE currencies
(
    id        INTEGER PRIMARY KEY AUTOINCREMENT,
    code      VARCHAR(3) UNIQUE NOT NULL,
    full_name VARCHAR(30)       NOT NULL,
    sign      VARCHAR(3)        NOT NULL
);

CREATE TABLE exchange_rates
(
    id                 INTEGER PRIMARY KEY AUTOINCREMENT,
    base_currency_id   INTEGER REFERENCES currencies (id) NOT NULL,
    target_currency_id INTEGER REFERENCES currencies (id) NOT NULL,
    rate               DECIMAL(6)                         NOT NULL
);