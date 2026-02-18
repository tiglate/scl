/*
DROP TABLE flyway_schema_history;
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
DROP TABLE tb_file_content;
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

IF OBJECT_ID('tb_file_content', 'U') IS NULL
BEGIN
    CREATE TABLE tb_file_content (
        id_file_content UNIQUEIDENTIFIER  NOT NULL,
        file_name       VARCHAR(500)      NOT NULL,
        file_type       VARCHAR(50)       NULL,
        content         VARBINARY(MAX)    NOT NULL,

        CONSTRAINT pk_file_content PRIMARY KEY (id_file_content)
    );

    CREATE TABLE tb_file_content_aud (
        id_file_content  UNIQUEIDENTIFIER  NOT NULL,
        id_revision      INT               NOT NULL,
        id_revision_type TINYINT           NULL,
        file_name        VARCHAR(500)      NULL,
        file_type        VARCHAR(50)       NULL,
        content          VARBINARY(MAX)    NULL,

        CONSTRAINT pk_file_content_aud PRIMARY KEY (id_file_content),
        CONSTRAINT fk_file_content_aud_revision FOREIGN KEY (id_revision) REFERENCES tb_revision (id_revision),
        CONSTRAINT fk_file_content_aud_revision_type FOREIGN KEY (id_revision_type) REFERENCES tb_revision_type (id_revision_type)
    );
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
        id_fx_settlement BIGINT           NOT NULL IDENTITY(1, 1),
        id_fx_trade      BIGINT           NOT NULL,
        --
        ins_flag         BIT              NOT NULL DEFAULT (0),
        ins_user_id      BIGINT           NULL,
        ins_timestamp    DATETIME2        NULL,
        ins_comments     VARCHAR(255)     NULL,
        ins_file_id      UNIQUEIDENTIFIER NULL,
        --
        g10_flag         BIT              NOT NULL DEFAULT (0),
        g10_user_id      BIGINT           NULL,
        g10_timestamp    DATETIME2        NULL,
        g10_comments     VARCHAR(255)     NULL,
        g10_file_id      UNIQUEIDENTIFIER NULL,
        --
        brl_flag         BIT              NOT NULL DEFAULT (0),
        brl_user_id      BIGINT           NULL,
        brl_timestamp    DATETIME2        NULL,
        brl_comments     VARCHAR(255)     NULL,
        brl_file_id      UNIQUEIDENTIFIER NULL,
        --
        ion_flag         BIT              NOT NULL DEFAULT (0),
        ion_user_id      BIGINT           NULL,
        ion_timestamp    DATETIME2        NULL,
        ion_comments     VARCHAR(255)     NULL,
        ion_file_id      UNIQUEIDENTIFIER NULL,
        --
        created_at       DATETIME2        NOT NULL DEFAULT (SYSDATETIME()),
        updated_at       DATETIME2        NULL,

        CONSTRAINT pk_fx_settlement PRIMARY KEY (id_fx_settlement),
        CONSTRAINT fk_fx_settlement_trade_id FOREIGN KEY (id_fx_trade) REFERENCES tb_fx_trade (id_fx_trade)
            ON UPDATE NO ACTION
            ON DELETE NO ACTION,
        CONSTRAINT fk_fx_settlement_file_content_ins FOREIGN KEY (ins_file_id) REFERENCES tb_file_content (id_file_content)
            ON UPDATE NO ACTION
            ON DELETE NO ACTION,
        CONSTRAINT fk_fx_settlement_file_content_g10 FOREIGN KEY (g10_file_id) REFERENCES tb_file_content (id_file_content)
            ON UPDATE NO ACTION
            ON DELETE NO ACTION,
        CONSTRAINT fk_fx_settlement_file_content_brl FOREIGN KEY (brl_file_id) REFERENCES tb_file_content (id_file_content)
            ON UPDATE NO ACTION
            ON DELETE NO ACTION,
        CONSTRAINT fk_fx_settlement_file_content_ion FOREIGN KEY (ion_file_id) REFERENCES tb_file_content (id_file_content)
            ON UPDATE NO ACTION
            ON DELETE NO ACTION,
        CONSTRAINT fk_fx_settlement_user_ins FOREIGN KEY (ins_user_id) REFERENCES tb_user (id_user)
            ON UPDATE NO ACTION
            ON DELETE NO ACTION,
        CONSTRAINT fk_fx_settlement_user_g10 FOREIGN KEY (g10_user_id) REFERENCES tb_user (id_user)
            ON UPDATE NO ACTION
            ON DELETE NO ACTION,
        CONSTRAINT fk_fx_settlement_user_brl FOREIGN KEY (brl_user_id) REFERENCES tb_user (id_user)
            ON UPDATE NO ACTION
            ON DELETE NO ACTION,
        CONSTRAINT fk_fx_settlement_user_ion FOREIGN KEY (ion_user_id) REFERENCES tb_user (id_user)
            ON UPDATE NO ACTION
            ON DELETE NO ACTION
    );

    CREATE TABLE tb_fx_settlement_aud (
        id_fx_settlement BIGINT           NOT NULL,
        id_revision      INT              NOT NULL,
        id_revision_type TINYINT          NULL,
        id_fx_trade      BIGINT           NULL,
        --
        ins_flag         BIT              NULL,
        ins_user_id      BIGINT           NULL,
        ins_timestamp    DATETIME2        NULL,
        ins_comments     VARCHAR(255)     NULL,
        ins_file_id      UNIQUEIDENTIFIER NULL,
        --
        g10_flag         BIT              NULL,
        g10_user_id      BIGINT           NULL,
        g10_timestamp    DATETIME2        NULL,
        g10_comments     VARCHAR(255)     NULL,
        g10_file_id      UNIQUEIDENTIFIER NULL,
        --
        brl_flag         BIT              NULL,
        brl_user_id      BIGINT           NULL,
        brl_timestamp    DATETIME2        NULL,
        brl_comments     VARCHAR(255)     NULL,
        brl_file_id      UNIQUEIDENTIFIER NULL,
        --
        ion_flag         BIT              NULL,
        ion_user_id      BIGINT           NULL,
        ion_timestamp    DATETIME2        NULL,
        ion_comments     VARCHAR(255)     NULL,
        ion_file_id      UNIQUEIDENTIFIER NULL,
        --
        created_at       DATETIME2        NULL,
        updated_at       DATETIME2        NULL,

        CONSTRAINT pk_fx_settlement_aud PRIMARY KEY (id_fx_settlement, id_revision),
        CONSTRAINT fk_fx_settlement_aud_revision FOREIGN KEY (id_revision) REFERENCES tb_revision (id_revision),
        CONSTRAINT fk_fx_settlement_aud_revision_type FOREIGN KEY (id_revision_type) REFERENCES tb_revision_type (id_revision_type)
    );
END

IF OBJECT_ID('tb_fx_settlement_log', 'U') IS NULL
BEGIN
    CREATE TABLE tb_fx_settlement_log (
        id_fx_settlement_log BIGINT           NOT NULL IDENTITY(1, 1),
        id_fx_settlement     BIGINT           NOT NULL,
        id_user              BIGINT           NOT NULL,
        id_file_content      UNIQUEIDENTIFIER NULL,
        step                 VARCHAR(30)      NOT NULL,
        flag                 BIT              NOT NULL,
        comments             VARCHAR(255)     NULL,
        event_date           DATETIME2        NOT NULL DEFAULT (SYSDATETIME()),

        CONSTRAINT pk_fx_settlement_log PRIMARY KEY (id_fx_settlement_log),
        CONSTRAINT fk_fx_settlement_log_fx_settlement FOREIGN KEY (id_fx_settlement) REFERENCES tb_fx_settlement (id_fx_settlement)
            ON UPDATE NO ACTION
            ON DELETE NO ACTION,
        CONSTRAINT fk_fx_settlement_log_user FOREIGN KEY (id_user) REFERENCES tb_user (id_user)
            ON UPDATE NO ACTION
            ON DELETE NO ACTION,
        CONSTRAINT fk_fx_settlement_log_file_content FOREIGN KEY (id_file_content) REFERENCES tb_file_content (id_file_content)
            ON UPDATE NO ACTION
            ON DELETE NO ACTION
    );
END