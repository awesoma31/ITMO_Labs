DROP TABLE IF EXISTS head CASCADE;
drop table if exists leg CASCADE;
drop table if exists body cascade;
drop table if exists construction cascade;
drop table if exists supporter cascade;
drop table if exists conflict cascade;
DROP TABLE IF EXISTS theory cascade;
DROP TABLE IF EXISTS preferred_construction cascade;
DROP TABLE IF EXISTS preferred_theory cascade;
-- alter table preferred_construction drop ;
drop type if exists skin_color_enum cascade;
drop type if exists hair_color_enum cascade;
drop type if exists face_types_enum cascade;

create type skin_color_enum as enum ('blue', 'green', 'white', 'black');
create type hair_color_enum as enum ('blue', 'brown', 'blond', 'black', 'red');
create type face_types_enum as enum ('round', 'square', 'oval', 'heart', 'rectangular', 'diamond');

CREATE TABLE head
(
    id         serial PRIMARY KEY,
    hair_color hair_color_enum not null,
    face_type  face_types_enum not null
);

CREATE TABLE body
(
    id            SERIAL PRIMARY KEY,
    skin_color    skin_color_enum,
    finger_amount int not null,
    arm_amount    int not null
);

CREATE TABLE leg
(
    id         SERIAL PRIMARY KEY,
    leg_amount int not null
);

CREATE TABLE construction
(
    id      serial PRIMARY KEY,
    head_id int references head,
    body_id int references body,
    legs_id int references leg
);

CREATE TABLE theory
(
    id          SERIAL PRIMARY KEY,
    name        text not null,
    description text
);

CREATE TABLE supporter
(
    id   serial PRIMARY KEY,
    name text not null
);

CREATE TABLE preferred_theory
(
    id           serial PRIMARY KEY,
    supporter_id INT UNIQUE REFERENCES supporter (id),
    theory_id    INT REFERENCES theory (id)
);

CREATE TABLE preferred_construction
(
    -- id              SERIAL PRIMARY KEY,
    construction_id INT not null REFERENCES construction (id),
    supporter_id    INT not null REFERENCES supporter (id),
        constraint preferred_construction_connection_id primary key (construction_id, supporter_id)
);
-- alter table preferred_construction add constraint preferred_construction_id primary key(supporter_id, construction_id);

INSERT INTO theory (name, description)
VALUES ('first', '5 fingers 2 arms'),
       ('second', '6 fingers 3 legs');

INSERT INTO head (hair_color, face_type)
VALUES ('blue', 'round'),
       ('black', 'oval');

INSERT INTO body (skin_color, finger_amount, arm_amount)
VALUES ('white', 5, 2),
       ('black', 6, 3);

INSERT INTO leg (leg_amount)
VALUES (2),
       (3);

INSERT INTO construction (head_id, body_id, legs_id)
VALUES (1, 1, 1),
       (2, 2, 2),
       (2, 1, 1);

INSERT INTO supporter (name)
VALUES ('Bob'),
       ('Greg'),
       ('Pimp');

INSERT INTO preferred_construction (construction_id, supporter_id)
VALUES (1, 1),
       (1, 2),
       (2, 3),
       (3, 3);

INSERT INTO preferred_theory (supporter_id, theory_id)
VALUES (1, 1),
       (2, 1),
       (3, 2);
