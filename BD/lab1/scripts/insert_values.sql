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