CREATE SEQUENCE primary_sequence START WITH 10000 INCREMENT BY 1;

CREATE TABLE [user] (
    id bigint NOT NULL,
    email varchar(255) NOT NULL,
    username varchar(30) NOT NULL,
    password varchar(255) NOT NULL,
    name varchar(255) NOT NULL,
    gender varchar(100) NOT NULL,
    is_active bit NOT NULL,
    resetuid uniqueidentifier,
    reset_start datetime2,
    CONSTRAINT PK_USER PRIMARY KEY (id)
);

CREATE TABLE role (
    id bigint NOT NULL,
    code varchar(50) NOT NULL,
    description varchar(255),
    CONSTRAINT PK_ROLE PRIMARY KEY (id)
);

CREATE TABLE counterparty (
    id bigint NOT NULL,
    origin_id int,
    long_name varchar(255) NOT NULL,
    short_name varchar(255),
    is_active bit NOT NULL,
    created_at datetime NOT NULL,
    updated_at datetime NOT NULL,
    updated_by_id bigint,
    CONSTRAINT PK_COUNTERPARTY PRIMARY KEY (id)
);

CREATE TABLE currency (
    id bigint NOT NULL,
    iso_code varchar(3) NOT NULL,
    bacen_code varchar(3) NOT NULL,
    name varchar(255) NOT NULL,
    CONSTRAINT PK_CURRENCY PRIMARY KEY (id)
);

CREATE TABLE fx_trade (
    id bigint NOT NULL,
    trade_id varchar(255),
    trade_date date,
    value_date date,
    product varchar(100),
    buy_amount numeric(20, 6),
    sell_amount numeric(20, 6),
    investor_manager varchar(255),
    beneficiary varchar(255),
    purpose varchar(100),
    created_at datetime NOT NULL,
    updated_at datetime NOT NULL,
    exchange_rate numeric(20, 10),
    counterparty_id bigint NOT NULL,
    buy_currency_id bigint,
    sell_currency_id bigint,
    updated_by_id bigint,
    CONSTRAINT PK_FX_TRADE PRIMARY KEY (id)
);

CREATE TABLE fx_settlement_step (
    id bigint NOT NULL,
    step varchar(100) NOT NULL,
    event_date datetime NOT NULL,
    comments varchar(255),
    user_id bigint NOT NULL,
    evidence_id bigint,
    CONSTRAINT PK_FX_SETTLEMENT_STEP PRIMARY KEY (id)
);

CREATE TABLE fx_settlement (
    id bigint NOT NULL,
    comments varchar(1000),
    failure_motive varchar(100) NOT NULL,
    failure_details varchar(1000),
    completed_at datetime NOT NULL,
    trade_id bigint NOT NULL,
    completed_by_id bigint,
    CONSTRAINT PK_FX_SETTLEMENT PRIMARY KEY (id)
);

CREATE TABLE fx_step_evidence (
    id bigint NOT NULL,
    [file] nvarchar(MAX),
    CONSTRAINT PK_FX_STEP_EVIDENCE PRIMARY KEY (id)
);

CREATE TABLE document_type (
    id bigint NOT NULL,
    name varchar(255) NOT NULL,
    CONSTRAINT PK_DOCUMENT_TYPE PRIMARY KEY (id)
);

CREATE TABLE document (
    id bigint NOT NULL,
    value varchar(255) NOT NULL,
    expiration date,
    document_type_id bigint,
    CONSTRAINT PK_DOCUMENT PRIMARY KEY (id)
);

CREATE TABLE file_content (
    uid varchar(255) NOT NULL,
    content varbinary(MAX) NOT NULL,
    CONSTRAINT PK_FILE_CONTENT PRIMARY KEY (uid)
);

CREATE TABLE user_role (
    user_id bigint NOT NULL,
    role_id bigint NOT NULL
);

CREATE TABLE fx_settlement_steps (
    fx_settlement_id bigint NOT NULL,
    fx_settlement_step_id bigint NOT NULL
);

CREATE TABLE counterparty_document (
    counterparty_id bigint NOT NULL,
    document_id bigint NOT NULL
);

ALTER TABLE [user] ADD CONSTRAINT unique_user_email UNIQUE (email);

ALTER TABLE [user] ADD CONSTRAINT unique_user_username UNIQUE (username);

ALTER TABLE role ADD CONSTRAINT unique_role_code UNIQUE (code);

ALTER TABLE counterparty ADD CONSTRAINT fk_counterparty_updated_by_id FOREIGN KEY (updated_by_id) REFERENCES [user] (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE currency ADD CONSTRAINT unique_currency_iso_code UNIQUE (iso_code);

ALTER TABLE currency ADD CONSTRAINT unique_currency_bacen_code UNIQUE (bacen_code);

ALTER TABLE fx_trade ADD CONSTRAINT fk_fx_trade_counterparty_id FOREIGN KEY (counterparty_id) REFERENCES counterparty (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE fx_trade ADD CONSTRAINT fk_fx_trade_buy_currency_id FOREIGN KEY (buy_currency_id) REFERENCES currency (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE fx_trade ADD CONSTRAINT fk_fx_trade_sell_currency_id FOREIGN KEY (sell_currency_id) REFERENCES currency (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE fx_trade ADD CONSTRAINT fk_fx_trade_updated_by_id FOREIGN KEY (updated_by_id) REFERENCES [user] (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE fx_settlement_step ADD CONSTRAINT fk_fx_settlement_step_user_id FOREIGN KEY (user_id) REFERENCES [user] (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE fx_settlement_step ADD CONSTRAINT fk_fx_settlement_step_evidence_id FOREIGN KEY (evidence_id) REFERENCES fx_step_evidence (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

CREATE UNIQUE NONCLUSTERED INDEX unique_fx_settlement_step_evidence_id ON fx_settlement_step(evidence_id) WHERE evidence_id IS NOT NULL;

ALTER TABLE fx_settlement ADD CONSTRAINT fk_fx_settlement_trade_id FOREIGN KEY (trade_id) REFERENCES fx_trade (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE fx_settlement ADD CONSTRAINT fk_fx_settlement_completed_by_id FOREIGN KEY (completed_by_id) REFERENCES [user] (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE document_type ADD CONSTRAINT unique_document_type_name UNIQUE (name);

ALTER TABLE document ADD CONSTRAINT fk_document_document_type_id FOREIGN KEY (document_type_id) REFERENCES document_type (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE user_role ADD CONSTRAINT pk_user_role PRIMARY KEY (user_id, role_id);

ALTER TABLE user_role ADD CONSTRAINT fk_user_role_user_id FOREIGN KEY (user_id) REFERENCES [user] (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE user_role ADD CONSTRAINT fk_user_role_role_id FOREIGN KEY (role_id) REFERENCES role (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE fx_settlement_steps ADD CONSTRAINT pk_fx_settlement_steps PRIMARY KEY (fx_settlement_id, fx_settlement_step_id);

ALTER TABLE fx_settlement_steps ADD CONSTRAINT fk_fx_settlement_steps_fx_settlement_id FOREIGN KEY (fx_settlement_id) REFERENCES fx_settlement (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE fx_settlement_steps ADD CONSTRAINT fk_fx_settlement_steps_fx_settlement_step_id FOREIGN KEY (fx_settlement_step_id) REFERENCES fx_settlement_step (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE counterparty_document ADD CONSTRAINT pk_counterparty_document PRIMARY KEY (counterparty_id, document_id);

ALTER TABLE counterparty_document ADD CONSTRAINT fk_counterparty_document_counterparty_id FOREIGN KEY (counterparty_id) REFERENCES counterparty (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE counterparty_document ADD CONSTRAINT fk_counterparty_document_document_id FOREIGN KEY (document_id) REFERENCES document (id) ON UPDATE NO ACTION ON DELETE NO ACTION;
