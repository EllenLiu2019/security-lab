-- Drop user first if they exist
DROP USER if exists 'security'@'localhost' ;

-- Now create user with prop privileges
CREATE USER 'security'@'localhost' IDENTIFIED BY 'security123456';

GRANT ALL PRIVILEGES ON * . * TO 'security'@'localhost';