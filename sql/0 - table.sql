/* 22-05-2025 */

--
-- USER TABLE
--
CREATE TABLE users (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    prefix VARCHAR(5) DEFAULT 'USR',     
    user_name VARCHAR(255) NOT NULL UNIQUE, -- identifiant
    user_password VARCHAR(255) NOT NULL,    -- hashed password
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP 
); 

--
-- ROLES : admin, manager, ...
--
CREATE TABLE roles (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    prefix VARCHAR(5) DEFAULT 'ROL',     
    label VARCHAR(255) NOT NULL,
    role_value INT NOT NULL,    -- DESC ROLE VALUE (admin will be 0, CEO will be 1 ... )
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP 
);



--
-- USER_ROLES : an user can have a role or multiple roles in the system
--
CREATE TABLE user_roles (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    prefix VARCHAR(5) DEFAULT 'USRL',     
    id_user BIGINT NOT NULL,
    id_role BIGINT NOT NULL,
    assigned_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);