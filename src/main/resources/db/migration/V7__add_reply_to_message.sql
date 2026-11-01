ALTER TABLE public.message ADD COLUMN reply_id uuid NULL;
CREATE INDEX idx_message_reply_id ON public.message USING btree (reply_id);
ALTER TABLE public.message ADD CONSTRAINT fk_message_reply_id FOREIGN KEY (reply_id) REFERENCES public.message(id) ON DELETE SET NULL;
