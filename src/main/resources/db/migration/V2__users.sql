CREATE TABLE users
(
    id integer NOT NULL,
    username character varying(255) COLLATE pg_catalog."default",
    hashed_password character varying(255) COLLATE pg_catalog."default",
    salt character varying(255) COLLATE pg_catalog."default",
    CONSTRAINT users_pkey PRIMARY KEY (id)
)

TABLESPACE pg_default;

ALTER TABLE clients
    OWNER to postgres;