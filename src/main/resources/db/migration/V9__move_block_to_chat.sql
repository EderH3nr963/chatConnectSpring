ALTER TABLE chat
    ADD COLUMN IF NOT EXISTS blocked boolean NOT NULL DEFAULT false,
    ADD COLUMN IF NOT EXISTS blocked_at timestamptz NULL;

ALTER TABLE chat DROP COLUMN IF EXISTS blocked_by;

CREATE INDEX IF NOT EXISTS idx_chat_blocked ON chat(blocked) WHERE blocked = true;

UPDATE friendship SET status = 'REJECTED' WHERE status = 'BLOCKED';
