-- Create database
CREATE DATABASE IF NOT EXISTS todo_app;
USE todo_app;

-- =========================
-- TABLE: users
-- =========================
DROP TABLE IF EXISTS users;

CREATE TABLE users (
                       id INT AUTO_INCREMENT PRIMARY KEY,
                       name VARCHAR(100),
                       email VARCHAR(150) UNIQUE,
                       password VARCHAR(255)
);

-- Insert sample users
INSERT INTO users (name, email, password) VALUES
                                              ('Aziz', 'aziz@mail.com', '1234'),
                                              ('Mariem', 'mariem@mail.com', '1234');

-- =========================
-- TABLE: tasks
-- =========================
DROP TABLE IF EXISTS tasks;

CREATE TABLE tasks (
                       id INT AUTO_INCREMENT PRIMARY KEY,
                       title VARCHAR(150),
                       description TEXT,
                       creation_date DATETIME,
                       due_date DATETIME,
                       status VARCHAR(20),
                       user_id INT,
                       FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Insert sample tasks
INSERT INTO tasks (title, description, creation_date, due_date, status, user_id) VALUES
                                                                                     ('Finish project', 'Complete Java project', NOW(), '2026-05-01 23:59:00', 'pending', 1),
                                                                                     ('Gym session', 'Leg day workout', NOW(), '2026-04-22 18:00:00', 'done', 2);