ALTER TABLE public.userblock ALTER COLUMN created_at TYPE timestamptz(6) USING created_at AT TIME ZONE 'UTC';
