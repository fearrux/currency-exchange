INSERT INTO currencies(code, full_name, sign)
VALUES ('AUD', 'Australian dollar', 'A$'),
       ('USD', 'US Dollar', '$'),
       ('AOA', 'Kwanza', 'Kz'),
       ('EUR', 'Euro', '€'),
       ('BRL', 'Brazilian Real', 'R$'),
       ('RUB', 'Russian Ruble', '₽'),
       ('BYN', 'Belarussian Ruble', 'Br'),
       ('BOV', 'Mvdol', 'Bs'),
       ('TRY', 'Turkish Lira', '₺'),
       ('CNY', 'Yuan Renminbi', '¥'),
       ('INR', 'Indian Rupee', '₹');

INSERT INTO exchange_rates(base_currency_id, target_currency_id, rate)
VALUES (2, 4, 0.96),
       (4, 2, 1.05),
       (4, 5, 6.48),
       (5, 4, 0.15),
       (6, 2, 0.0096),
       (2, 6, 104.67),
       (10, 1, 0.2173),
       (1, 10, 0.22),
       (7, 8, 5.07),
       (8, 7, 0.44);