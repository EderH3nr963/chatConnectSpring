ALTER TABLE public."user" DROP CONSTRAINT IF EXISTS idx_user_clerk_user_id;
DROP INDEX IF EXISTS public.idx_user_clerk_user_id;
ALTER TABLE public."user" DROP COLUMN IF EXISTS clerk_user_id;
