/*
DROP TABLE flyway_schema_history;
DROP TABLE tb_file_content;
DROP TABLE tb_fx_settlement_steps;
DROP TABLE tb_fx_settlement_steps_aud;
DROP TABLE tb_fx_settlement_step;
DROP TABLE tb_fx_settlement_step_aud;
DROP TABLE tb_fx_step_evidence;
DROP TABLE tb_fx_step_evidence_aud;
DROP TABLE tb_fx_settlement;
DROP TABLE tb_fx_settlement_aud;
DROP TABLE tb_fx_trade;
DROP TABLE tb_fx_trade_aud;
DROP TABLE tb_currency;
DROP TABLE tb_currency_aud;
DROP TABLE tb_document;
DROP TABLE tb_document_aud;
DROP TABLE tb_counterparty;
DROP TABLE tb_counterparty_aud;
DROP TABLE tb_document_type;
DROP TABLE tb_document_type_aud;
DROP TABLE tb_user_role;
DROP TABLE tb_user_role_aud;
DROP TABLE tb_role;
DROP TABLE tb_role_aud;
DROP TABLE tb_user;
DROP TABLE tb_user_aud;
DROP TABLE tb_department;
DROP TABLE tb_department_aud;
DROP TABLE tb_revision_type;
DROP TABLE tb_revision;
*/

IF OBJECT_ID('tb_revision', 'U') IS NULL
BEGIN
    CREATE TABLE tb_revision (
        id_revision     INT       NOT NULL IDENTITY(1, 1),
        id_user         BIGINT    NULL,
        event_timestamp DATETIME2 NOT NULL,

        CONSTRAINT pk_revision PRIMARY KEY (id_revision)
    );
END

IF OBJECT_ID('tb_revision_type', 'U') IS NULL
BEGIN
    CREATE TABLE tb_revision_type (
        id_revision_type TINYINT     NOT NULL,
        description      VARCHAR(50) NOT NULL,

        CONSTRAINT pk_revision_type PRIMARY KEY (id_revision_type)
    );

    INSERT INTO tb_revision_type (id_revision_type, description) VALUES
    (0, 'ADD'),
    (1, 'MOD'),
    (2, 'DEL');
END

IF OBJECT_ID('tb_department', 'U') IS NULL
BEGIN
    CREATE TABLE tb_department (
        id_department BIGINT       NOT NULL IDENTITY(1, 1),
        name          VARCHAR(255) NOT NULL,
        email         VARCHAR(255) NULL,
        created_at    DATETIME2    NOT NULL DEFAULT(SYSDATETIME()),
        updated_at    DATETIME2    NULL,

        CONSTRAINT pk_department PRIMARY KEY CLUSTERED (id_department),
        CONSTRAINT qa_department_name UNIQUE (name)
    );

    CREATE TABLE tb_department_aud (
        id_department    BIGINT        NOT NULL,
        id_revision      INT           NOT NULL,
        id_revision_type TINYINT       NULL,
        name             VARCHAR(255)  NULL,
        email            VARCHAR(255)  NULL,
        created_at       DATETIME2     NULL,
        updated_at       DATETIME2     NULL,

        CONSTRAINT pk_department_aud PRIMARY KEY (id_department, id_revision),
        CONSTRAINT fk_department_aud_revision FOREIGN KEY (id_revision) REFERENCES tb_revision (id_revision),
        CONSTRAINT fk_department_aud_revision_type FOREIGN KEY (id_revision_type) REFERENCES tb_revision_type (id_revision_type)
    );
END

IF OBJECT_ID('tb_user', 'U') IS NULL
BEGIN
    CREATE TABLE tb_user (
        id_user       BIGINT           NOT NULL IDENTITY(1, 1),
        id_department BIGINT           NOT NULL,
        name          VARCHAR(255)     NOT NULL,
        email         VARCHAR(255)     NULL,
        gender        VARCHAR(10)      NOT NULL,
        username      VARCHAR(50)      NOT NULL,
        password      VARCHAR(255)     NOT NULL,
        enabled       BIT              NOT NULL DEFAULT(1),
        resetuid      UNIQUEIDENTIFIER NULL,
        reset_start   DATETIME2        NULL,
        created_at    DATETIME2        NOT NULL DEFAULT(SYSDATETIME()),
        updated_at    DATETIME2        NULL,

        CONSTRAINT pk_user            PRIMARY KEY NONCLUSTERED (id_user),
        CONSTRAINT uq_user_username   UNIQUE CLUSTERED (username),
        CONSTRAINT uq_user_email      UNIQUE (email),
        CONSTRAINT fk_user_department FOREIGN KEY (id_department) REFERENCES tb_department (id_department)
    );

    CREATE TABLE tb_user_aud (
        id_user          BIGINT           NOT NULL,
        id_revision      INT              NOT NULL,
        id_revision_type TINYINT          NULL,
        id_department    BIGINT           NULL,
        name             VARCHAR(255)     NULL,
        email            VARCHAR(255)     NULL,
        gender           VARCHAR(10)      NULL,
        username         VARCHAR(50)      NULL,
        password         VARCHAR(255)     NULL,
        enabled          BIT              NULL,
        resetuid         UNIQUEIDENTIFIER NULL,
        reset_start      DATETIME2        NULL,
        created_at       DATETIME2        NULL,
        updated_at       DATETIME2        NULL,

        CONSTRAINT pk_user_aud PRIMARY KEY (id_user, id_revision),
        CONSTRAINT fk_user_aud_revision FOREIGN KEY (id_revision) REFERENCES tb_revision (id_revision),
        CONSTRAINT fk_user_aud_revision_type FOREIGN KEY (id_revision_type) REFERENCES tb_revision_type (id_revision_type)
    );
END

IF OBJECT_ID('tb_role', 'U') IS NULL
BEGIN
    CREATE TABLE tb_role (
        id_role     BIGINT       NOT NULL IDENTITY (1, 1),
        code        VARCHAR(255) NOT NULL,
        description VARCHAR(255) NULL,
        created_at  DATETIME2    NOT NULL DEFAULT (SYSDATETIME()),
        updated_at  DATETIME2    NULL,

        CONSTRAINT pk_role PRIMARY KEY CLUSTERED (id_role),
        CONSTRAINT uq_role_code UNIQUE (code)
    );

    CREATE TABLE tb_role_aud (
        id_role          BIGINT       NOT NULL,
        id_revision      INT          NOT NULL,
        id_revision_type TINYINT      NULL,
        code             VARCHAR(255) NULL,
        description      VARCHAR(255) NULL,
        created_at       DATETIME2    NULL,
        updated_at       DATETIME2    NULL,

        CONSTRAINT pk_role_aud PRIMARY KEY (id_role, id_revision),
        CONSTRAINT fk_role_aud_revision FOREIGN KEY (id_revision) REFERENCES tb_revision (id_revision),
        CONSTRAINT fk_role_aud_revision_type FOREIGN KEY (id_revision_type) REFERENCES tb_revision_type (id_revision_type)
    );
END

IF OBJECT_ID('tb_user_role', 'U') IS NULL
BEGIN
    CREATE TABLE tb_user_role (
        id_user BIGINT NOT NULL,
        id_role BIGINT NOT NULL,

        CONSTRAINT pk_user_role PRIMARY KEY (id_user, id_role),
        CONSTRAINT fk_user_role_user FOREIGN KEY (id_user) REFERENCES tb_user (id_user)
            ON UPDATE CASCADE
            ON DELETE CASCADE,
        CONSTRAINT fk_user_role_role FOREIGN KEY (id_role) REFERENCES tb_role (id_role)
            ON UPDATE CASCADE
            ON DELETE CASCADE
    );

    CREATE TABLE tb_user_role_aud (
        id_user          BIGINT  NOT NULL,
        id_role          BIGINT  NOT NULL,
        id_revision      INT     NOT NULL,
        id_revision_type TINYINT NULL,

        CONSTRAINT pk_user_role_aud PRIMARY KEY (id_user, id_role, id_revision),
        CONSTRAINT fk_user_role_aud_revision FOREIGN KEY (id_revision) REFERENCES tb_revision (id_revision),
        CONSTRAINT fk_user_role_aud_revision_type FOREIGN KEY (id_revision_type) REFERENCES tb_revision_type (id_revision_type)
    );
END

IF OBJECT_ID('tb_currency', 'U') IS NULL
BEGIN
    CREATE TABLE tb_currency (
        id_currency BIGINT       NOT NULL IDENTITY(1, 1),
        iso_code    VARCHAR(3)   NOT NULL,
        bacen_code  VARCHAR(3)   NOT NULL,
        name        VARCHAR(255) NOT NULL,
        end_date    DATE         NULL,
        created_at  DATETIME2    NOT NULL DEFAULT(SYSDATETIME()),
        updated_at  DATETIME2    NULL,

        CONSTRAINT pk_currency PRIMARY KEY (id_currency),
        CONSTRAINT uq_currency_iso_code UNIQUE (iso_code),
        CONSTRAINT uq_currency_bacen_code UNIQUE (bacen_code)
    );

    CREATE TABLE tb_currency_aud (
        id_currency      BIGINT       NOT NULL,
        id_revision      INT          NOT NULL,
        id_revision_type TINYINT      NULL,
        iso_code         VARCHAR(3)   NULL,
        bacen_code       VARCHAR(3)   NULL,
        name             VARCHAR(255) NULL,
        end_date         DATE         NULL,
        created_at       DATETIME2    NULL,
        updated_at       DATETIME2    NULL,

        CONSTRAINT pk_currency_aud PRIMARY KEY (id_currency, id_revision),
        CONSTRAINT fk_currency_aud_revision FOREIGN KEY (id_revision) REFERENCES tb_revision (id_revision),
        CONSTRAINT fk_currency_aud_revision_type FOREIGN KEY (id_revision_type) REFERENCES tb_revision_type (id_revision_type)
    );
END

IF OBJECT_ID('tb_document_type', 'U') IS NULL
BEGIN
    CREATE TABLE tb_document_type (
        id_document_type BIGINT       NOT NULL IDENTITY (1, 1),
        name             VARCHAR(255) NOT NULL,
        created_at       DATETIME2    NOT NULL DEFAULT (SYSDATETIME()),
        updated_at       DATETIME2    NULL,

        CONSTRAINT pk_document_type PRIMARY KEY (id_document_type),
        CONSTRAINT uq_document_type_name UNIQUE (name)
    );

    CREATE TABLE tb_document_type_aud (
        id_document_type BIGINT       NOT NULL,
        id_revision      INT          NOT NULL,
        id_revision_type TINYINT      NULL,
        name             VARCHAR(255) NULL,
        created_at       DATETIME2    NULL,
        updated_at       DATETIME2    NULL,

        CONSTRAINT pk_document_type_aud PRIMARY KEY (id_document_type, id_revision),
        CONSTRAINT fk_document_type_aud_revision FOREIGN KEY (id_revision) REFERENCES tb_revision (id_revision),
        CONSTRAINT fk_document_type_aud_revision_type FOREIGN KEY (id_revision_type) REFERENCES tb_revision_type (id_revision_type)
    );
END

IF OBJECT_ID('tb_counterparty', 'U') IS NULL
BEGIN
    CREATE TABLE tb_counterparty (
        id_counterparty BIGINT       NOT NULL IDENTITY(1, 1),
        id_updated_by   BIGINT       NULL,
        origin_id       INT          NULL,
        long_name       VARCHAR(255) NOT NULL,
        short_name      VARCHAR(255) NULL,
        is_active       BIT          NOT NULL DEFAULT (1),
        created_at      DATETIME2    NOT NULL DEFAULT (SYSDATETIME()),
        updated_at      DATETIME2    NULL,

        CONSTRAINT pk_counterparty PRIMARY KEY (id_counterparty),
        CONSTRAINT fk_counterparty_user FOREIGN KEY (id_updated_by) REFERENCES tb_user (id_user)
            ON UPDATE NO ACTION
            ON DELETE NO ACTION
    );

    CREATE TABLE tb_counterparty_aud (
        id_counterparty  BIGINT       NOT NULL,
        id_revision      INT          NOT NULL,
        id_revision_type TINYINT      NULL,
        id_updated_by    BIGINT       NULL,
        origin_id        INT          NULL,
        long_name        VARCHAR(255) NULL,
        short_name       VARCHAR(255) NULL,
        is_active        BIT          NULL,
        created_at       DATETIME2    NULL,
        updated_at       DATETIME2    NULL,

        CONSTRAINT pk_counterparty_aud PRIMARY KEY (id_counterparty, id_revision),
        CONSTRAINT fk_counterparty_aud_revision FOREIGN KEY (id_revision) REFERENCES tb_revision (id_revision),
        CONSTRAINT fk_counterparty_aud_revision_type FOREIGN KEY (id_revision_type) REFERENCES tb_revision_type (id_revision_type)
    );
END

IF OBJECT_ID('tb_document', 'U') IS NULL
BEGIN
    CREATE TABLE tb_document (
        id_document      BIGINT       NOT NULL IDENTITY (1, 1),
        id_counterparty  BIGINT       NOT NULL,
        id_document_type BIGINT       NOT NULL,
        value            VARCHAR(255) NOT NULL,
        expiration       DATE         NULL,
        created_at       DATETIME2    NOT NULL DEFAULT (SYSDATETIME()),
        updated_at       DATETIME2    NULL,

        CONSTRAINT pk_document PRIMARY KEY (id_document),
        CONSTRAINT fk_document_counterparty FOREIGN KEY (id_counterparty) REFERENCES tb_counterparty (id_counterparty)
            ON UPDATE CASCADE
            ON DELETE CASCADE,
        CONSTRAINT fk_document_document_type FOREIGN KEY (id_document_type) REFERENCES tb_document_type (id_document_type)
            ON UPDATE NO ACTION
            ON DELETE NO ACTION
    );

    CREATE TABLE tb_document_aud (
        id_document      BIGINT       NOT NULL,
        id_revision      INT          NOT NULL,
        id_counterparty  BIGINT       NULL,
        id_revision_type TINYINT      NULL,
        id_document_type BIGINT       NULL,
        value            VARCHAR(255) NULL,
        expiration       DATE         NULL,
        created_at       DATETIME2    NULL,
        updated_at       DATETIME2    NULL,

        CONSTRAINT pk_document_aud PRIMARY KEY (id_document, id_revision),
        CONSTRAINT fk_document_aud_revision FOREIGN KEY (id_revision) REFERENCES tb_revision (id_revision),
        CONSTRAINT fk_document_aud_revision_type FOREIGN KEY (id_revision_type) REFERENCES tb_revision_type (id_revision_type)
    );
END

IF OBJECT_ID('tb_fx_trade', 'U') IS NULL
BEGIN
    CREATE TABLE tb_fx_trade (
        id_fx_trade      BIGINT          NOT NULL IDENTITY(1, 1),
        id_counterparty  BIGINT          NOT NULL,
        id_buy_currency  BIGINT          NOT NULL,
        id_sell_currency BIGINT          NOT NULL,
        id_updated_by    BIGINT          NULL,
        trade_id         VARCHAR(255)    NULL,
        trade_date       DATE            NULL     DEFAULT (GETDATE()),
        value_date       DATE            NULL,
        product          VARCHAR(100)    NULL,
        buy_amount       NUMERIC(20, 6)  NULL,
        sell_amount      NUMERIC(20, 6)  NULL,
        exchange_rate    NUMERIC(20, 10) NULL,
        investor_manager VARCHAR(255)    NULL,
        beneficiary      VARCHAR(255)    NULL,
        purpose          VARCHAR(100)    NULL,
        created_at       DATETIME2       NOT NULL DEFAULT (SYSDATETIME()),
        updated_at       DATETIME2       NULL,

        CONSTRAINT pk_fx_trade PRIMARY KEY (id_fx_trade),
        CONSTRAINT fk_fx_trade_counterparty FOREIGN KEY (id_counterparty) REFERENCES tb_counterparty (id_counterparty)
            ON UPDATE NO ACTION
            ON DELETE NO ACTION,
        CONSTRAINT fk_fx_trade_currency_buy FOREIGN KEY (id_buy_currency) REFERENCES tb_currency (id_currency)
            ON UPDATE NO ACTION
            ON DELETE NO ACTION,
        CONSTRAINT fk_fx_trade_currency_sell FOREIGN KEY (id_sell_currency) REFERENCES tb_currency (id_currency)
            ON UPDATE NO ACTION
            ON DELETE NO ACTION,
        CONSTRAINT fk_fx_trade_user FOREIGN KEY (id_updated_by) REFERENCES tb_user (id_user)
            ON UPDATE NO ACTION
            ON DELETE NO ACTION
    );

    CREATE TABLE tb_fx_trade_aud (
        id_fx_trade      BIGINT          NOT NULL,
        id_revision      INT             NOT NULL,
        id_revision_type TINYINT         NULL,
        id_counterparty  BIGINT          NULL,
        id_buy_currency  BIGINT          NULL,
        id_sell_currency BIGINT          NULL,
        id_updated_by    BIGINT          NULL,
        trade_id         VARCHAR(255)    NULL,
        trade_date       DATE            NULL,
        value_date       DATE            NULL,
        product          VARCHAR(100)    NULL,
        buy_amount       NUMERIC(20, 6)  NULL,
        sell_amount      NUMERIC(20, 6)  NULL,
        exchange_rate    NUMERIC(20, 10) NULL,
        investor_manager VARCHAR(255)    NULL,
        beneficiary      VARCHAR(255)    NULL,
        purpose          VARCHAR(100)    NULL,
        created_at       DATETIME2       NULL,
        updated_at       DATETIME2       NULL,

        CONSTRAINT pk_fx_trade_aud PRIMARY KEY (id_fx_trade, id_revision),
        CONSTRAINT fk_fx_trade_aud_revision FOREIGN KEY (id_revision) REFERENCES tb_revision (id_revision),
        CONSTRAINT fk_fx_trade_aud_revision_type FOREIGN KEY (id_revision_type) REFERENCES tb_revision_type (id_revision_type)
    );
END

IF OBJECT_ID('tb_fx_settlement', 'U') IS NULL
BEGIN
    CREATE TABLE tb_fx_settlement (
        id_fx_settlement BIGINT    NOT NULL IDENTITY(1, 1),
        id_fx_trade      BIGINT    NOT NULL,
        created_at       DATETIME2 NOT NULL DEFAULT (SYSDATETIME()),
        updated_at       DATETIME2 NULL,

        CONSTRAINT pk_fx_settlement PRIMARY KEY (id_fx_settlement),
        CONSTRAINT fk_fx_settlement_trade_id FOREIGN KEY (id_fx_trade) REFERENCES tb_fx_trade (id_fx_trade)
            ON UPDATE NO ACTION
            ON DELETE NO ACTION
    );

    CREATE TABLE tb_fx_settlement_aud (
        id_fx_settlement BIGINT    NOT NULL,
        id_revision      INT       NOT NULL,
        id_revision_type TINYINT   NULL,
        id_fx_trade      BIGINT    NULL,
        created_at       DATETIME2 NULL,
        updated_at       DATETIME2 NULL,

        CONSTRAINT pk_fx_settlement_aud PRIMARY KEY (id_fx_settlement, id_revision),
        CONSTRAINT fk_fx_settlement_aud_revision FOREIGN KEY (id_revision) REFERENCES tb_revision (id_revision),
        CONSTRAINT fk_fx_settlement_aud_revision_type FOREIGN KEY (id_revision_type) REFERENCES tb_revision_type (id_revision_type)
    );
END

IF OBJECT_ID('tb_fx_step_evidence', 'U') IS NULL
BEGIN
    CREATE TABLE tb_fx_step_evidence (
        id_fx_step_evidence BIGINT        NOT NULL,
        [file]              NVARCHAR(MAX) NOT NULL,

        CONSTRAINT pk_fx_step_evidence PRIMARY KEY (id_fx_step_evidence)
    );

    CREATE TABLE tb_fx_step_evidence_aud (
        id_fx_step_evidence BIGINT        NOT NULL,
        id_revision         INT           NOT NULL,
        id_revision_type    TINYINT       NULL,
        [file]              NVARCHAR(MAX) NULL,

        CONSTRAINT pk_fx_step_evidence_aud PRIMARY KEY (id_fx_step_evidence, id_revision),
        CONSTRAINT fk_fx_step_evidence_aud_revision FOREIGN KEY (id_revision) REFERENCES tb_revision (id_revision),
        CONSTRAINT fk_fx_step_evidence_aud_revision_type FOREIGN KEY (id_revision_type) REFERENCES tb_revision_type (id_revision_type)
    );
END

IF OBJECT_ID('tb_fx_settlement_step', 'U') IS NULL
BEGIN
    CREATE TABLE tb_fx_settlement_step (
        id_fx_settlement_step BIGINT       NOT NULL IDENTITY(1, 1),
        id_user               BIGINT       NOT NULL,
        id_evidence           BIGINT       NULL,
        step                  VARCHAR(100) NOT NULL,
        event_date            DATETIME     NOT NULL DEFAULT GETDATE(),
        comments              VARCHAR(255) NULL,

        CONSTRAINT pk_fx_settlement_step PRIMARY KEY (id_fx_settlement_step),
        CONSTRAINT fk_fx_settlement_step_user FOREIGN KEY (id_user) REFERENCES tb_user (id_user)
            ON UPDATE NO ACTION
            ON DELETE NO ACTION,
        CONSTRAINT fk_fx_settlement_step_evidence FOREIGN KEY (id_evidence) REFERENCES tb_fx_step_evidence (id_fx_step_evidence)
            ON UPDATE NO ACTION
            ON DELETE NO ACTION
    );
    CREATE UNIQUE NONCLUSTERED INDEX unique_fx_settlement_step_evidence_id ON tb_fx_settlement_step (id_evidence) WHERE id_evidence IS NOT NULL;

    CREATE TABLE tb_fx_settlement_step_aud (
        id_fx_settlement_step BIGINT       NOT NULL,
        id_revision           INT          NOT NULL,
        id_revision_type      TINYINT      NULL,
        id_user               BIGINT       NULL,
        id_evidence           BIGINT       NULL,
        step                  VARCHAR(100) NULL,
        event_date            DATETIME     NULL,
        comments              VARCHAR(255) NULL,

        CONSTRAINT pk_fx_settlement_step_aud PRIMARY KEY (id_fx_settlement_step, id_revision),
        CONSTRAINT fk_fx_settlement_step_aud_revision FOREIGN KEY (id_revision) REFERENCES tb_revision (id_revision),
        CONSTRAINT fk_fx_settlement_step_aud_revision_type FOREIGN KEY (id_revision_type) REFERENCES tb_revision_type (id_revision_type)
    );
END

IF OBJECT_ID('tb_fx_settlement_steps', 'U') IS NULL
BEGIN
    CREATE TABLE tb_fx_settlement_steps (
        id_fx_settlement      BIGINT NOT NULL,
        id_fx_settlement_step BIGINT NOT NULL,

        CONSTRAINT pk_fx_settlement_steps PRIMARY KEY (id_fx_settlement, id_fx_settlement_step),
        CONSTRAINT fk_fx_settlement_steps_fx_settlement FOREIGN KEY (id_fx_settlement) REFERENCES tb_fx_settlement (id_fx_settlement)
            ON UPDATE CASCADE
            ON DELETE CASCADE,
        CONSTRAINT fk_fx_settlement_steps_fx_settlement_step FOREIGN KEY (id_fx_settlement_step) REFERENCES tb_fx_settlement_step (id_fx_settlement_step)
            ON UPDATE CASCADE
            ON DELETE CASCADE
    );

    CREATE TABLE tb_fx_settlement_steps_aud (
        id_fx_settlement      BIGINT  NOT NULL,
        id_fx_settlement_step BIGINT  NOT NULL,
        id_revision           INT     NOT NULL,
        id_revision_type      TINYINT NULL,

        CONSTRAINT pk_fx_settlement_steps_aud PRIMARY KEY (id_fx_settlement, id_fx_settlement_step, id_revision),
        CONSTRAINT fk_fx_settlement_steps_aud_revision FOREIGN KEY (id_revision) REFERENCES tb_revision (id_revision),
        CONSTRAINT fk_fx_settlement_steps_aud_revision_type FOREIGN KEY (id_revision_type) REFERENCES tb_revision_type (id_revision_type)
    );
END

IF OBJECT_ID('tb_file_content', 'U') IS NULL
BEGIN
    CREATE TABLE tb_file_content (
        uid     VARCHAR(255)   NOT NULL,
        content VARBINARY(MAX) NOT NULL,

        CONSTRAINT pk_file_content PRIMARY KEY (uid)
    );
END