/* 22-05-2025 */

INSERT INTO roles (label, role_value) VALUES ('ADMIN', 0);
INSERT INTO roles (label, role_value) VALUES ('CEO', 1);
INSERT INTO roles (label, role_value) VALUES ('HEAD OF DEPARTEMENT', 2);
INSERT INTO roles (label, role_value) VALUES ('MANAGER', 3);
INSERT INTO roles (label, role_value) VALUES ('ACCOUNTANT', 4);

--
--
--

INSERT INTO users (user_name, user_password) VALUES ('admin', crypt('admin', gen_salt('bf')));
INSERT INTO user_roles (id_user, id_role) VALUES (2, 1); -- admin is user id 2