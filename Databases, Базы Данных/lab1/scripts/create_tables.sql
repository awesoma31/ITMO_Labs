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
    id              SERIAL PRIMARY KEY,
    construction_id INT REFERENCES construction (id),
    supporter_id    INT REFERENCES supporter (id)
);


-- CREATE TABLE "Construction" (
--   "id" integer,
--   "head_id" integer,
--   "body_id" integer,
--   "leg_id" integer
-- );
--
-- CREATE TABLE "Head" (
--   "id" integer,
--   "hair_color" text,
--   "face_type" text
-- );
--
-- CREATE TABLE "Body" (
--   "id" integer,
--   "arm_amount" integer,
--   "finger_amount" integer,
--   "skin_color" int
-- );
--
-- CREATE TABLE "Leg" (
--   "id" integer,
--   "leg_amount" integer
-- );
--
-- CREATE TABLE "supporters" (
--   "id" serial,
--   "name" text
-- );
--
-- CREATE TABLE "theory" (
--   "id" int,
--   "name" text,
--   "description" text
-- );
--
-- CREATE TABLE "preferred_theories" (
--   "id" int,
--   "supporter_id" int,
--   "theory_id" int
-- );
--
-- CREATE TABLE "preferred_construction" (
--   "id" int,
--   "construction_id" int,
--   "supporter_id" int
-- );
--
-- ALTER TABLE "Construction" ADD FOREIGN KEY ("head_id") REFERENCES "Head" ("id");
--
-- ALTER TABLE "Construction" ADD FOREIGN KEY ("body_id") REFERENCES "Body" ("id");
--
-- ALTER TABLE "Construction" ADD FOREIGN KEY ("leg_id") REFERENCES "Leg" ("id");
--
-- CREATE TABLE "supporters_preferred_theories" (
--   "supporters_id" serial,
--   "preferred_theories_supporter_id" int,
--   PRIMARY KEY ("supporters_id", "preferred_theories_supporter_id")
-- );
--
-- ALTER TABLE "supporters_preferred_theories" ADD FOREIGN KEY ("supporters_id") REFERENCES "supporters" ("id");
--
-- ALTER TABLE "supporters_preferred_theories" ADD FOREIGN KEY ("preferred_theories_supporter_id") REFERENCES "preferred_theories" ("supporter_id");
--
--
-- ALTER TABLE "preferred_theories" ADD FOREIGN KEY ("theory_id") REFERENCES "theory" ("id");
--
-- CREATE TABLE "Construction_preferred_construction" (
--   "Construction_id" integer,
--   "preferred_construction_construction_id" int,
--   PRIMARY KEY ("Construction_id", "preferred_construction_construction_id")
-- );
--
-- ALTER TABLE "Construction_preferred_construction" ADD FOREIGN KEY ("Construction_id") REFERENCES "Construction" ("id");
--
-- ALTER TABLE "Construction_preferred_construction" ADD FOREIGN KEY ("preferred_construction_construction_id") REFERENCES "preferred_construction" ("construction_id");
--
--
-- CREATE TABLE "supporters_preferred_construction" (
--   "supporters_id" serial,
--   "preferred_construction_supporter_id" int,
--   PRIMARY KEY ("supporters_id", "preferred_construction_supporter_id")
-- );
--
-- ALTER TABLE "supporters_preferred_construction" ADD FOREIGN KEY ("supporters_id") REFERENCES "supporters" ("id");
--
-- ALTER TABLE "supporters_preferred_construction" ADD FOREIGN KEY ("preferred_construction_supporter_id") REFERENCES "preferred_construction" ("supporter_id");
--
