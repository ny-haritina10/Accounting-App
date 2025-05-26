/* 22-05-2025 */

--
-- USER TABLE
--
CREATE TABLE users (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    prefix VARCHAR(5) DEFAULT 'USR',     
    user_name VARCHAR(255) NOT NULL UNIQUE, -- identifiant
    user_password VARCHAR(255) NOT NULL,    -- hashed password using 
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

/* 26-05-2025 */

--
-- account types
--
CREATE TABLE account_types (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    prefix VARCHAR(5) DEFAULT 'ACT',
    label VARCHAR(20) NOT NULL CHECK (label IN ('ASSET', 'LIABILITY', 'EQUITY', 'REVENUE', 'EXPENSE')),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

--
-- chart of accounts
--
CREATE TABLE chart_of_accounts (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    prefix VARCHAR(5) DEFAULT 'COA',
    account_code VARCHAR(10) NOT NULL,
    account_name VARCHAR(255) NOT NULL,
    id_account_type BIGINT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_account_type FOREIGN KEY (id_account_type) REFERENCES account_types(id)
);


--
-- JOURNAL ENTRIES
--
CREATE TABLE journal_entries (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    prefix VARCHAR(5) DEFAULT 'JRN',
    entry_description VARCHAR(255) NOT NULL,
    entry_status VARCHAR(20) NOT NULL CHECK (entry_status IN ('CREATED', 'VALIDATED', 'CANCELED', 'REVERSED')),
    id_user BIGINT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_user FOREIGN KEY (id_user) REFERENCES users(id)
);

--
-- JOURNAL ENTRY LINES
-- 
CREATE TABLE journal_entry_lines (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    prefix VARCHAR(5) DEFAULT 'JEL',
    id_journal_entry BIGINT NOT NULL,
    id_account BIGINT NOT NULL,
    debit DECIMAL(15, 2) DEFAULT 0.00,
    credit DECIMAL(15, 2) DEFAULT 0.00,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_journal_entry FOREIGN KEY (id_journal_entry) REFERENCES journal_entries(id),
    CONSTRAINT fk_account FOREIGN KEY (id_account) REFERENCES accounts(id),
    CONSTRAINT chk_debit_credit CHECK (debit >= 0 AND credit >= 0 AND (debit > 0 OR credit > 0))
);