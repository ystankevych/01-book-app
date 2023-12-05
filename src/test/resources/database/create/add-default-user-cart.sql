INSERT INTO users (id, email, password, first_name, last_name, shipping_address)
VALUES (1, 'user@ukr.net', '123456', 'user', 'user', 'ukraine');

INSERT INTO users_roles (user_id, role_id) VALUES (1, 1), (1, 2);

INSERT INTO shopping_carts (id) values (1);