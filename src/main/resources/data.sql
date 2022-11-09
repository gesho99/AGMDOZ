CREATE TABLE model (version VARCHAR(8) PRIMARY KEY, training_set_size INT, ts TIMESTAMP);
CREATE TABLE chat_message (
    id INT PRIMARY KEY,
    chatId INT,
    userId INT NOT NULL,
    model_version VARCHAR(4),
    ts TIMESTAMP NOT NULL,
    text VARCHAR(255),
    type VARCHAR(128)
);