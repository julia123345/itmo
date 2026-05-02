CREATE DATABASE IF NOT EXISTS auth_db;
CREATE DATABASE IF NOT EXISTS geometry_db;

-- The docker image already creates appuser/password for MYSQL_DATABASE,
-- but we also grant privileges for both schemas here.
GRANT ALL PRIVILEGES ON auth_db.* TO 'appuser'@'%';
GRANT ALL PRIVILEGES ON geometry_db.* TO 'appuser'@'%';
FLUSH PRIVILEGES;

USE auth_db;

CREATE TABLE IF NOT EXISTS users (
    login VARCHAR(50) PRIMARY KEY,
    password_hash VARCHAR(255) NOT NULL,
    created TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

USE geometry_db;

CREATE TABLE IF NOT EXISTS results (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    login VARCHAR(50) NOT NULL,
    x DOUBLE NOT NULL,
    y DOUBLE NOT NULL,
    r DOUBLE NOT NULL,
    hit BOOLEAN NOT NULL,
    created TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);