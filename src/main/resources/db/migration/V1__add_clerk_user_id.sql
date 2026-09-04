alter table "user"
    add column if not exists clerk_user_id varchar(255);

update "user"
set clerk_user_id = coalesce(clerk_user_id, 'legacy-' || id::text)
where clerk_user_id is null;

alter table "user"
    alter column clerk_user_id set not null;

alter table "user"
    alter column password drop not null;

create unique index if not exists idx_user_clerk_user_id on "user" (clerk_user_id);
