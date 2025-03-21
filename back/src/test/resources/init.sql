
DROP TABLE IF EXISTS `PARTICIPATE`;
DROP TABLE IF EXISTS `SESSIONS`;
DROP TABLE IF EXISTS `TEACHERS`;
DROP TABLE IF EXISTS `USERS`;

CREATE TABLE `TEACHERS` (
  `id` INT PRIMARY KEY AUTO_INCREMENT,
  `last_name` VARCHAR(40),
  `first_name` VARCHAR(40),
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE `SESSIONS` (
  `id` INT PRIMARY KEY AUTO_INCREMENT,
  `name` VARCHAR(50),
  `description` VARCHAR(2000),
  `date` TIMESTAMP,
  `teacher_id` int,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE `USERS` (
  `id` INT PRIMARY KEY AUTO_INCREMENT,
  `last_name` VARCHAR(40),
  `first_name` VARCHAR(40),
  `admin` BOOLEAN NOT NULL DEFAULT false,
  `email` VARCHAR(255),
  `password` VARCHAR(255),
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE `PARTICIPATE` (
  `user_id` INT, 
  `session_id` INT
);

ALTER TABLE `SESSIONS` ADD FOREIGN KEY (`teacher_id`) REFERENCES `TEACHERS` (`id`);
ALTER TABLE `PARTICIPATE` ADD FOREIGN KEY (`user_id`) REFERENCES `USERS` (`id`);
ALTER TABLE `PARTICIPATE` ADD FOREIGN KEY (`session_id`) REFERENCES `SESSIONS` (`id`);

INSERT INTO USERS (id, email, first_name, last_name, password, admin) VALUES (1, 'jdoe@mx.com', 'John', 'Doe', '$2a$10$gt3kCqD91prrb79zoSjig..aoDPHbXyDYJvqvn7oBaMQ/tdrAAgbW',true);
INSERT INTO USERS (id, email, first_name, last_name, password) VALUES (2, 'jsmith@mx.com', 'John', 'Smith', '$2a$10$gt3kCqD91prrb79zoSjig..aoDPHbXyDYJvqvn7oBaMQ/tdrAAgbW');
INSERT INTO TEACHERS (id, last_name, first_name) VALUES (1, 'Willis', 'Bruce');
INSERT INTO TEACHERS (id, last_name, first_name) VALUES (2, 'Carrey', 'Jim');
INSERT INTO SESSIONS (id, name, description, date, teacher_id) VALUES (1, 'Yoga Session', 'This is a yoga session',CURRENT_TIMESTAMP, 1);
INSERT INTO SESSIONS (id, name, description, date, teacher_id) VALUES (2, 'Another Yoga Session', 'This is another yoga session',CURRENT_TIMESTAMP, 2);
INSERT INTO PARTICIPATE (user_id,session_id) VALUES (2,2);

