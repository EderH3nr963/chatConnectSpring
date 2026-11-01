-- Outbox para deleção assíncrona de objetos do S3 após delete de mensagens com anexo.
-- Resolve dupla escrita: delete da message + outbox na mesma transação; worker apaga do S3 fora da request.
CREATE TYPE s3_cleanup_status AS ENUM ('PENDING', 'DONE', 'FAILED');

CREATE TABLE s3_cleanup_outbox (
    id               UUID PRIMARY KEY,
    s3_key           VARCHAR(512) NOT NULL,
    message_id       UUID NULL,
    chat_id          UUID NULL,
    status           s3_cleanup_status NOT NULL DEFAULT 'PENDING',
    attempts         INT NOT NULL DEFAULT 0,
    last_error       VARCHAR(1000) NULL,
    created_at       TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at       TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    processed_at     TIMESTAMPTZ NULL
);

CREATE INDEX idx_s3_outbox_status_created ON s3_cleanup_outbox (status, created_at);
CREATE INDEX idx_s3_outbox_s3_key ON s3_cleanup_outbox (s3_key);
