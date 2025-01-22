INSERT INTO subscriptions (id, username, user_full_name, is_active) VALUES
('1e0727e4-9aaa-412c-a09f-80dbd50dcb12', 'vova', 'John Doe', true),
('120727e4-9aaa-412c-a09f-80dbd50dcb12', 'vasya','Jane Smith', true);

INSERT INTO books (id, title, author, publication_date, subscription_id) VALUES
('1e0727e4-9aaa-412c-a09f-80dbd50dcb67', 'Effective Java', 'Joshua Bloch', '2018-01-01', null),
('1e0727e4-9aaa-412c-a09f-80dbd50dcb12', 'Effective Java 2', 'Joshua Bloch', '2018-01-01', '120727e4-9aaa-412c-a09f-80dbd50dcb12'),
('1e0727e4-9aaa-412c-a09f-80dbd50dcb23', 'Effective Java 2', 'Joshua Bloch', '2018-01-01', '120727e4-9aaa-412c-a09f-80dbd50dcb12');
