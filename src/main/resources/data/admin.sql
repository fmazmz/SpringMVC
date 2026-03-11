-- password: 'admin123'
INSERT INTO users (id, email, password, role)
VALUES (gen_random_uuid(), 'admin@email.com', '$2a$12$8OWlR1mHZsObyRQSGQNsHepoa.t2uiyhbHUZC2g5wzb9TGXawtsS.', 'ADMIN');

-- password: 'admin123'
INSERT INTO users (id, email, password, role)
VALUES ('f503c304-afed-4931-975d-5886439df667', 'user@email.com', '$2a$12$8OWlR1mHZsObyRQSGQNsHepoa.t2uiyhbHUZC2g5wzb9TGXawtsS.', 'APP_USER');