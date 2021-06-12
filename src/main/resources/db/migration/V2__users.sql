CREATE TABLE users
(
    id integer NOT NULL,
    username character varying(255) COLLATE pg_catalog."default",
    hashed_password character varying(255) COLLATE pg_catalog."default",
    salt character varying(255) COLLATE pg_catalog."default",
    role INTEGER NOT NULL,
    connected_record_id integer NOT NULL,
    CONSTRAINT users_pkey PRIMARY KEY (id)
)

    TABLESPACE pg_default;

ALTER TABLE users
    OWNER to postgres;