CREATE TYPE attachment_type AS ENUM ('TEXT', 'IMAGE', 'DOCUMENT', 'AUDIO', 'VIDEO');
CREATE TYPE message_status AS ENUM ('PENDING', 'SENT');

CREATE TABLE public.friendship (
	id uuid NOT NULL,
	accepted_at timestamptz(6) NULL,
	another_user_id uuid NOT NULL,
	invited_at timestamptz(6) NULL,
	status varchar(255) NOT NULL,
	updated_at timestamptz(6) NULL,
	user_id uuid NOT NULL,
	CONSTRAINT friendship_pkey PRIMARY KEY (id),
	CONSTRAINT friendship_status_check CHECK (((status)::text = ANY ((ARRAY['ACCEPTED'::character varying, 'PENDING'::character varying, 'REJECTED'::character varying])::text[])))
);
CREATE INDEX idx_friendship_another_user_id ON public.friendship USING btree (another_user_id);
CREATE INDEX idx_friendship_composite ON public.friendship USING btree (user_id, another_user_id);
CREATE INDEX idx_friendship_status ON public.friendship USING btree (status);
CREATE INDEX idx_friendship_user_id ON public.friendship USING btree (user_id);

CREATE TABLE public.chat (
    id uuid NOT NULL,
    avatar_key varchar(255) NULL,
    chat_type varchar(255) NULL,
    created_at timestamptz(6) NULL,
    description varchar(1000) NULL,
    name varchar(255) NULL,
    updated_at timestamptz(6) NULL,
    CONSTRAINT chat_chat_type_check CHECK (((chat_type)::text = ANY ((ARRAY['PRIVATE'::character varying, 'GROUP'::character varying])::text[]))),
	CONSTRAINT chat_pkey PRIMARY KEY (id)
);

CREATE TABLE public."user" (
	id uuid NOT NULL,
	clerk_user_id varchar(255) NOT NULL,
	created_at timestamptz(6) NULL,
	email varchar(255) NOT NULL,
	updated_at timestamptz(6) NULL,
	username varchar(255) NULL,
	CONSTRAINT idx_user_clerk_user_id UNIQUE (clerk_user_id),
	CONSTRAINT idx_user_email UNIQUE (email),
	CONSTRAINT user_pkey PRIMARY KEY (id)
);

CREATE TABLE public.chat_participant (
	id uuid NOT NULL,
	chat_id uuid NOT NULL,
	fixed_chat bool NULL,
	joined_at timestamptz(6) NULL,
	"role" varchar(255) NULL,
	unread_messages int4 NULL,
	user_id uuid NOT NULL,
	username varchar(255) NULL,
	CONSTRAINT chat_participant_pkey PRIMARY KEY (id),
	CONSTRAINT chat_participant_role_check CHECK (((role)::text = ANY ((ARRAY['ADMIN'::character varying, 'DEFAULT'::character varying])::text[]))),
	CONSTRAINT uk_chat_participant_user_chat UNIQUE (chat_id, user_id),
	CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES public."user"(id)
);
CREATE INDEX idx_chat_participant_chat_id ON public.chat_participant USING btree (chat_id);
CREATE INDEX idx_chat_participant_user_id ON public.chat_participant USING btree (user_id);

CREATE TABLE public.userblock (
	id uuid NOT NULL,
	blocked_id uuid NOT NULL,
	blocker_id uuid NOT NULL,
	created_at timestamptz(6) NOT NULL,
	CONSTRAINT userblock_pkey PRIMARY KEY (id),
	CONSTRAINT fk_blocked_id FOREIGN KEY (blocked_id) REFERENCES public."user"(id)
);
CREATE INDEX idx_userblock_blocker_id ON public.userblock USING btree (blocker_id);
CREATE INDEX idx_userblock_composite ON public.userblock USING btree (blocker_id, blocked_id);

CREATE TABLE public.message (
	id uuid NOT NULL,
	chat_id uuid NOT NULL,
	"content" varchar(4000) NOT NULL,
	created_at timestamptz(6) NULL,
	sender_id uuid NOT NULL,
	updated_at timestamptz(6) NULL,
    status message_status NOT NULL DEFAULT 'SENT',
    type attachment_type NOT NULL DEFAULT 'TEXT',
    file_s3_key VARCHAR(512),
    file_name VARCHAR(255),
    file_size BIGINT,
    file_mime_type VARCHAR(255);
	CONSTRAINT message_pkey PRIMARY KEY (id)
);
CREATE INDEX idx_message_chat_created ON public.message USING btree (chat_id, created_at);
CREATE INDEX idx_message_sender_id ON public.message USING btree (sender_id);

ALTER TABLE public.message ADD CONSTRAINT fk49xxdqy6p6kxedwvpjyuvnkf FOREIGN KEY (sender_id) REFERENCES public."user"(id);
ALTER TABLE public.message ADD CONSTRAINT fkmejd0ykokrbuekwwgd5a5xt8a FOREIGN KEY (chat_id) REFERENCES public.chat(id);