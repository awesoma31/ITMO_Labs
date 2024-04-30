create table if not exists users
(
    id       serial primary key,
    username text unique not null,
    password text
);

create table if not exists Coordinates
(
    id       serial primary key,
    x        real,
    y        bigint not null check ( y < 117 ),
    owner_id int references users (id)
);

create table if not exists Person
(
    id          serial primary key,
    name        text      not null,
    birthday    timestamp not null,
    weight      float check ( weight > 0 ),
    eye_color   text check ( eye_color in ('RED', 'BLACK', 'YELLOW', 'ORANGE', 'WHITE')),
    nationality text      not null check ( nationality in ('UNITED_KINGDOM', 'GERMANY', 'FRANCE') ),
    owner_id    int references users (id)
);

create table if not exists Movie
(
    id             serial primary key,
    name           text                       not null check ( length(name) > 0 ),
    coordinates_id int references Coordinates not null,
    creationDate   timestamp                  not null,
    oscarsCount    int                        not null check ( oscarsCount > 0 ),
    totalBoxOffice int check ( totalBoxOffice > 0 ),
    usaBoxOffice   bigint check ( usaBoxOffice > 0 ),
    genre          text
        CONSTRAINT genre_check check ( genre in
                                       ('MUSICAL', 'THRILLER', 'FANTASY', 'SCIENCE_FICTION', 'COMEDY', 'HORROR') ),
    operator_id    int references person      not null,
    owner_id       int references users (id)
);
