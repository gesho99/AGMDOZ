CREATE TABLE chat_message (
    id INT PRIMARY KEY,
    chatId VARCHAR(128),
    userId INT NOT NULL,
    model_version VARCHAR(4),
    ts TIMESTAMP NOT NULL,
    text VARCHAR(255),
    type VARCHAR(128)
);