CREATE TABLE pull_request (
    id            BIGSERIAL PRIMARY KEY,
    repo_full_name VARCHAR(255) NOT NULL,
    pr_number     INT NOT NULL,
    title         VARCHAR(500),
    created_at    TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (repo_full_name, pr_number)
);

CREATE TABLE review (
    id              BIGSERIAL PRIMARY KEY,
    pull_request_id BIGINT NOT NULL REFERENCES pull_request(id),
    head_sha        VARCHAR(64) NOT NULL,
    status          VARCHAR(20) NOT NULL,
    content         TEXT,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (pull_request_id, head_sha)
);