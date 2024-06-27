CREATE TABLE feed (
    id SERIAL PRIMARY KEY,
    feedts BIGINT,
    feedisodate VARCHAR(25),
    source VARCHAR(100),
    title TEXT,
    url TEXT,
    content TEXT,
    updated BIGINT,
    isodate VARCHAR(25),
    score DOUBLE PRECISION DEFAULT 0.0
);
