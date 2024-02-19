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
