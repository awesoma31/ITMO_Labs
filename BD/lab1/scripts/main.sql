-- BEGIN;

drop table if exists heads cascade;
drop table if exists legs cascade;
drop table if exists bodies cascade;
drop table if exists constructions cascade;
drop table if exists supporters cascade;
drop table if exists conflict cascade;

drop type if exists skin_color_enum cascade;
drop type if exists hair_color_enum cascade;
drop type if exists face_types_enum cascade;

create type skin_color_enum as enum ('blue', 'green', 'white', 'black');
create type hair_color_enum as enum ('blue', 'brown', 'blond', 'black', 'red');
create type face_types_enum as enum ('round', 'square', 'oval', 'heart', 'rectangular', 'diamond');

create table heads
(
    head_id    serial PRIMARY KEY,
    hair_color hair_color_enum not null,
    face_type  face_types_enum not null
);

create table bodies
(
    head_id       SERIAL PRIMARY KEY,
    skin_color    skin_color_enum,
    finger_amount int not null,
    arm_amount    int not null
);

create table legs
(
    leg_id     SERIAL PRIMARY KEY,
    leg_amount int not null
);

CREATE TABLE constructions
(
    construction_id serial PRIMARY KEY,
    name            text not null,
    head_id         int references heads,
    body_id         int references bodies,
    legs_id         int references legs
);

CREATE TABLE supporters
(
    supporter_id    serial PRIMARY KEY,
    name            text not null,
    construction_id int references constructions
);

CREATE TABLE conflict
(
    conflict_id      serial PRIMARY KEY,
    disputant_id     int references supporters,
    construction_id  int references constructions,
    preferred_theory text
);

INSERT INTO heads (hair_color, face_type)
VALUES ('blue', 'round'),
       ('black', 'oval');

INSERT INTO bodies (skin_color, finger_amount, arm_amount)
VALUES ('white', 5, 2),
       ('black', 6, 3);

INSERT INTO legs (leg_amount)
VALUES (2),
       (3);

INSERT INTO constructions (name, head_id, body_id, legs_id)
VALUES ('Human', 1, 1, 1),
       ('Cat', 2, 2, 2);

INSERT INTO supporters (name, construction_id)
VALUES ('Bob', 1),
       ('Greg', 2);


-- COMMIT;