IF NOT EXISTS(SELECT 1 FROM tb_department)
BEGIN
    INSERT INTO tb_department (name) VALUES ('IT');
    INSERT INTO tb_department (name) VALUES ('Business Risk Management');
    INSERT INTO tb_department (name) VALUES ('Client Desk');
    INSERT INTO tb_department (name) VALUES ('Compliance');
    INSERT INTO tb_department (name) VALUES ('Enterprise Risk Management');
    INSERT INTO tb_department (name) VALUES ('FLDS');
    INSERT INTO tb_department (name) VALUES ('Finance');
    INSERT INTO tb_department (name) VALUES ('Legal');
    INSERT INTO tb_department (name) VALUES ('Middle Office');
    INSERT INTO tb_department (name) VALUES ('Operations');
    INSERT INTO tb_department (name) VALUES ('Trading Desk');
END

IF NOT EXISTS(SELECT 1 FROM tb_role)
BEGIN
    INSERT INTO tb_role (code) VALUES ('ADMIN');
    INSERT INTO tb_role (code) VALUES ('SETTLEMENT_READ');
    INSERT INTO tb_role (code) VALUES ('SETTLEMENT_WRITE');
    INSERT INTO tb_role (code) VALUES ('COUNTERPARTY_READ');
    INSERT INTO tb_role (code) VALUES ('COUNTERPARTY_WRITE');
    INSERT INTO tb_role (code) VALUES ('TRADE_READ');
    INSERT INTO tb_role (code) VALUES ('TRADE_WRITE');
    INSERT INTO tb_role (code) VALUES ('DEPARTMENT_READ');
    INSERT INTO tb_role (code) VALUES ('DEPARTMENT_WRITE');
END

IF NOT EXISTS(SELECT 1 FROM tb_user)
BEGIN
    DECLARE @Id_User INT

    INSERT INTO tb_user (
        id_department,
        name,
        email,
        gender,
        username,
        password,
        is_active)
    SELECT
        id_department,
        name      = 'Admin',
        email     = 'admin@admin.com',
        gender    = 'MALE',
        username  = 'admin',
        password  = '{bcrypt}$2a$12$NYZurvH.l.vujYDufA6X6uFLBqQ1tDSDxX5VPTAcKSpNxJ3mBiWOW', -- 12345
        is_active = 1
    FROM
        tb_department
    WHERE
        name = 'IT'

    SET @Id_User = SCOPE_IDENTITY()

    INSERT INTO tb_user_role (id_user, id_role)
    SELECT
        @Id_User,
        id_role
    FROM
        tb_role
END