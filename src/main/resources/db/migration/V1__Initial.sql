CREATE SEQUENCE hibernate_sequence START 1;

CREATE TABLE clients
(
    id integer NOT NULL,
    name character varying(255) COLLATE pg_catalog."default",
    surname character varying(255) COLLATE pg_catalog."default",
    CONSTRAINT clients_pkey PRIMARY KEY (id)
)

    TABLESPACE pg_default;

ALTER TABLE clients
    OWNER to postgres;

CREATE TABLE offices
(
    id integer NOT NULL,
    name character varying(255) COLLATE pg_catalog."default",
    CONSTRAINT offices_pkey PRIMARY KEY (id)
)

    TABLESPACE pg_default;

ALTER TABLE offices
    OWNER to postgres;


CREATE TABLE pets
(
    id integer NOT NULL,
    animal integer,
    birthday date,
    name character varying(255) COLLATE pg_catalog."default",
    owner_id integer,
    CONSTRAINT pets_pkey PRIMARY KEY (id),
    CONSTRAINT fkia3j8k8vy70lln2x4rqy8yi4w FOREIGN KEY (owner_id)
        REFERENCES clients (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)

    TABLESPACE pg_default;

ALTER TABLE pets
    OWNER to postgres;

CREATE TABLE vets
(
    id integer NOT NULL,
    name character varying(255) COLLATE pg_catalog."default",
    office_hours_end time without time zone,
    office_hours_start time without time zone,
    photo bytea,
    surname character varying(255) COLLATE pg_catalog."default",
    CONSTRAINT vets_pkey PRIMARY KEY (id)
)

    TABLESPACE pg_default;

ALTER TABLE vets
    OWNER to postgres;

CREATE TABLE visits
(
    id integer NOT NULL,
    duration interval,
    price numeric(19,2),
    start_date timestamp without time zone,
    status integer,
    description character varying(255) COLLATE pg_catalog."default",
    CONSTRAINT visits_pkey PRIMARY KEY (id)
)

    TABLESPACE pg_default;

ALTER TABLE visits
    OWNER to postgres;

CREATE TABLE office
(
    office_id integer,
    id integer NOT NULL,
    CONSTRAINT office_pkey PRIMARY KEY (id),
    CONSTRAINT fk6o5niq7gfytyndsbvq3pieydb FOREIGN KEY (office_id)
        REFERENCES offices (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT fkofg4b4somx7hwnnxmr51574v1 FOREIGN KEY (id)
        REFERENCES visits (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)

    TABLESPACE pg_default;

ALTER TABLE office
    OWNER to postgres;

CREATE TABLE pet
(
    pet_id integer,
    id integer NOT NULL,
    CONSTRAINT pet_pkey PRIMARY KEY (id),
    CONSTRAINT fk38tmgw5erj979mwj8o37yqsfp FOREIGN KEY (pet_id)
        REFERENCES pets (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT fkiiep5diftr9flqn6ifbxhdawo FOREIGN KEY (id)
        REFERENCES visits (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)

    TABLESPACE pg_default;

ALTER TABLE pet
    OWNER to postgres;

CREATE TABLE vet
(
    vet_id integer,
    id integer NOT NULL,
    CONSTRAINT vet_pkey PRIMARY KEY (id),
    CONSTRAINT fk240o8qbm9lmg28al18payxp30 FOREIGN KEY (id)
        REFERENCES visits (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT fk8oq4tt2t5lxiwjwjq32greusy FOREIGN KEY (vet_id)
        REFERENCES vets (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)

    TABLESPACE pg_default;

ALTER TABLE vet
    OWNER to postgres;


