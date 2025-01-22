create table subscriptions
(
    id                  uuid primary key,
    username            text unique nulls not distinct,
    user_full_name      text,
    is_active           boolean,
    subscription_date   timestamp
);

create table books
(
    id                  uuid primary key,
    author              text,
    title               text,
    subscription_id     uuid references subscriptions (id)
);

create table shedlock (
                          name varchar(64),
                          lock_until timestamp(3) NULL,
                          locked_at timestamp(3) NULL,
                          locked_by varchar(255),
                          primary key (name)
)