ALTER TABLE public."user" DROP COLUMN IF EXISTS is_active_2fa;
ALTER TABLE public.auth_credentials ADD COLUMN is_active_2fa BOOLEAN NOT NULL DEFAULT FALSE;
