create table clients
(
    id      integer not null
        constraint clients_pkey
            primary key,
    name    varchar(255),
    surname varchar(255)
);

create table offices
(
    id   integer not null
        constraint offices_pkey
            primary key,
    name varchar(255)
);

create table pets
(
    id       integer not null
        constraint pets_pkey
            primary key,
    animal   integer,
    birthday date,
    name     varchar(255),
    owner_id integer
        constraint fkia3j8k8vy70lln2x4rqy8yi4w
            references clients
);

create table vets
(
    id                 integer not null
        constraint vets_pkey
            primary key,
    name               varchar(255),
    office_hours_end   time,
    office_hours_start time,
    photo              bytea,
    surname            varchar(255)
);

create table visits
(
    id         integer not null
        constraint visits_pkey
            primary key,
    duration   interval,
    price      numeric(19, 2),
    start_date timestamp,
    status     integer
);

create table office
(
    office_id integer
        constraint fk6o5niq7gfytyndsbvq3pieydb
            references offices,
    id        integer not null
        constraint office_pkey
            primary key
        constraint fkofg4b4somx7hwnnxmr51574v1
            references visits
);

create table pet
(
    pet_id integer
        constraint fk38tmgw5erj979mwj8o37yqsfp
            references pets,
    id     integer not null
        constraint pet_pkey
            primary key
        constraint fkiiep5diftr9flqn6ifbxhdawo
            references visits
);

create table vet
(
    vet_id integer
        constraint fk8oq4tt2t5lxiwjwjq32greusy
            references vets,
    id     integer not null
        constraint vet_pkey
            primary key
        constraint fk240o8qbm9lmg28al18payxp30
            references visits
);
