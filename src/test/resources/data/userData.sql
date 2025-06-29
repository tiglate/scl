INSERT INTO [user] (
    id,
    email,
    username,
    password,
    name,
    gender,
    is_active
) VALUES (
    1000,
    'Sed ut perspiciatis.',
    'admin@invalid.bootify.io',
    '{bcrypt}$2a$10$FMzmOkkfbApEWxS.4XzCKOR7EbbiwzkPEyGgYh6uQiPxurkpzRMa6',
    'Zed diam voluptua.',
    'MALE',
    1
);

INSERT INTO user_role (
    user_id,
    role_id
) VALUES (
    1000,
    1100
);

INSERT INTO [user] (
    id,
    email,
    username,
    password,
    name,
    gender,
    is_active
) VALUES (
    1001,
    'Lorem ipsum dolor.',
    'settlementRead@x.x',
    '{bcrypt}$2a$10$FMzmOkkfbApEWxS.4XzCKOR7EbbiwzkPEyGgYh6uQiPxurkpzRMa6',
    'At vero eos.',
    'MALE',
    0
);

INSERT INTO user_role (
    user_id,
    role_id
) VALUES (
    1001,
    1101
);

INSERT INTO [user] (
    id,
    email,
    username,
    password,
    name,
    gender,
    is_active
) VALUES (
    1002,
    'Duis autem vel.',
    'settlementWrite@x.x',
    '{bcrypt}$2a$10$FMzmOkkfbApEWxS.4XzCKOR7EbbiwzkPEyGgYh6uQiPxurkpzRMa6',
    'Stet clita kasd.',
    'MALE',
    1
);

INSERT INTO user_role (
    user_id,
    role_id
) VALUES (
    1002,
    1102
);

INSERT INTO [user] (
    id,
    email,
    username,
    password,
    name,
    gender,
    is_active
) VALUES (
    1003,
    'Ut wisi enim.',
    'counterpartyRead@x.x',
    '{bcrypt}$2a$10$FMzmOkkfbApEWxS.4XzCKOR7EbbiwzkPEyGgYh6uQiPxurkpzRMa6',
    'No sea takimata.',
    'MALE',
    0
);

INSERT INTO user_role (
    user_id,
    role_id
) VALUES (
    1003,
    1103
);

INSERT INTO [user] (
    id,
    email,
    username,
    password,
    name,
    gender,
    is_active
) VALUES (
    1004,
    'Nam liber tempor.',
    'counterpartyWrite@x.x',
    '{bcrypt}$2a$10$FMzmOkkfbApEWxS.4XzCKOR7EbbiwzkPEyGgYh6uQiPxurkpzRMa6',
    'Vel illum dolore.',
    'MALE',
    1
);

INSERT INTO user_role (
    user_id,
    role_id
) VALUES (
    1004,
    1104
);

INSERT INTO [user] (
    id,
    email,
    username,
    password,
    name,
    gender,
    is_active
) VALUES (
    1005,
    'Consetetur sadipscing.',
    'tradeRead@invalid.bootify.io',
    '{bcrypt}$2a$10$FMzmOkkfbApEWxS.4XzCKOR7EbbiwzkPEyGgYh6uQiPxurkpzRMa6',
    'Xonsectetuer adipiscing.',
    'MALE',
    0
);

INSERT INTO user_role (
    user_id,
    role_id
) VALUES (
    1005,
    1105
);

INSERT INTO [user] (
    id,
    email,
    username,
    password,
    name,
    gender,
    is_active
) VALUES (
    1006,
    'Xed diam nonumy.',
    'tradeWrite@invalid.bootify.io',
    '{bcrypt}$2a$10$FMzmOkkfbApEWxS.4XzCKOR7EbbiwzkPEyGgYh6uQiPxurkpzRMa6',
    'Quis nostrud exerci.',
    'MALE',
    1
);

INSERT INTO user_role (
    user_id,
    role_id
) VALUES (
    1006,
    1106
);
