create table subscriptions
(
    id                  uuid primary key,
    username            text unique nulls not distinct,
    user_full_name      text,
    is_active           boolean
);

create table books
(
    id                  uuid primary key,
    author              text,
    title               text,
    publication_date    timestamp,
    subscription_id     uuid references subscriptions (id)
);

create index idx_books_subscription_id on books (subscription_id);


create table shedlock (
                          name varchar(64),
                          lock_until timestamp(3) NULL,
                          locked_at timestamp(3) NULL,
                          locked_by varchar(255),
                          primary key (name)
)