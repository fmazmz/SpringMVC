-- password: 'admin123'
INSERT INTO users (id, email, password, role)
VALUES (gen_random_uuid(), 'admin@email.com', '$2a$12$8OWlR1mHZsObyRQSGQNsHepoa.t2uiyhbHUZC2g5wzb9TGXawtsS.', 'ADMIN');