CREATE SCHEMA IF NOT EXISTS PUBLIC;
DROP TABLE IF EXISTS AK_SOCIAL;
DROP TABLE IF EXISTS AK_POSITION;
DROP TABLE IF EXISTS AK_PUSH;
DROP TABLE IF EXISTS AK_NOTIFICATION;
DROP TABLE IF EXISTS AK_USER_AUTHORITY;
DROP TABLE IF EXISTS AK_USER;
DROP TABLE IF EXISTS AK_AUTHORITY;
DROP TABLE IF EXISTS AK_USER_IMAGE;
DROP TABLE IF EXISTS AK_CHAT;
DROP TABLE IF EXISTS AK_CHAT_USER;
DROP TABLE IF EXISTS AK_CHAT_LINE;


CREATE TABLE AK_AUTHORITY
(
    ID   INT AUTO_INCREMENT PRIMARY KEY,
    NAME VARCHAR(255) NOT NULL,
    CONSTRAINT AUTHORITY_NAME_U UNIQUE (NAME)
);


CREATE TABLE AK_POSITION
(
    ID         INT AUTO_INCREMENT PRIMARY KEY,
    LAT        DECIMAL                             NOT NULL,
    LON        DECIMAL                             NOT NULL,
    CREATED_AT TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    UPDATED_AT TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE TABLE AK_PUSH
(
    ID         INT AUTO_INCREMENT PRIMARY KEY,
    USER_ID    VARCHAR(255)                        NOT NULL,
    PUSH_TOKEN VARCHAR(255)                        NOT NULL,
    CREATED_AT TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    UPDATED_AT TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE TABLE AK_USER
(
    ID                  INT AUTO_INCREMENT PRIMARY KEY,
    FIRST_NAME          VARCHAR(255)                        NOT NULL,
    LAST_NAME           VARCHAR(255)                        NOT NULL,
    EMAIL               VARCHAR(255)                        NOT NULL,
    PASSWORD_HASH       VARCHAR(255),
    IMG_URL             VARCHAR(255),
    BIRTHDAY            DATE                                NOT NULL,
    BIO                 TEXT,
    GENDER              INT COMMENT '1-male,0-female',
    CREATED_AT          TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    UPDATED_AT          TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    ACCOUNT_EXPIRED     BOOLEAN   DEFAULT FALSE,
    ACCOUNT_LOCKED      BOOLEAN   DEFAULT FALSE,
    CREDENTIALS_EXPIRED BOOLEAN   DEFAULT FALSE,
    ENABLED             BOOLEAN   DEFAULT TRUE,
    POSITION_ID         INT,
    PUSH_ID             INT,
    CONSTRAINT USER_EMAIL_U UNIQUE (EMAIL),
    CONSTRAINT USER_ID_FK
        FOREIGN KEY (POSITION_ID) REFERENCES AK_POSITION (ID)
            ON
                DELETE CASCADE
            ON
                UPDATE CASCADE,
    CONSTRAINT PUSH_ID_FK
        FOREIGN KEY (PUSH_ID) REFERENCES AK_PUSH (ID)
            ON
                DELETE CASCADE
            ON
                UPDATE CASCADE,
);

-- SELECT (6371 * acos(
--                 cos(radians(lat2))
--                 * cos(radians(lat1))
--                 * cos(radians(lng1) - radians(lng2))
--             + sin(radians(lat2))
--                     * sin(radians(lat1))
--     )) as distance
-- from your_table

CREATE TABLE AK_SOCIAL
(
    ID          INT AUTO_INCREMENT PRIMARY KEY,
    USER_ID     INT                                 NOT NULL,
    SOCIAL_ID   VARCHAR(255)                        NOT NULL,
    SOCIAL_TYPE INT                                 NOT NULL,
    EMAIL       VARCHAR(255)                        NOT NULL,
    CREATED_AT  TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    UPDATED_AT  TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT SOCIAL_USER_ID_SOCIAL_ID_U UNIQUE (USER_ID, SOCIAL_ID),
    CONSTRAINT SOCIAL_USER_ID_FK
        FOREIGN KEY (USER_ID) REFERENCES AK_USER (ID)
            ON
                DELETE CASCADE
            ON
                UPDATE CASCADE,
    CONSTRAINT SOCIAL_EMAIL_U UNIQUE (EMAIL)
);


CREATE TABLE AK_USER_AUTHORITY
(
    ID           INT AUTO_INCREMENT PRIMARY KEY,
    USER_ID      INT NOT NULL,
    AUTHORITY_ID INT NOT NULL,
    CONSTRAINT USER_AUTHORITY_USER_ID_AUTHORITY_ID_U UNIQUE (USER_ID, AUTHORITY_ID),
    CONSTRAINT USER_AUTHORITY_USER_ID_FK
        FOREIGN KEY (USER_ID) REFERENCES AK_USER (ID)
            ON
                DELETE CASCADE
            ON
                UPDATE CASCADE,
    CONSTRAINT USER_AUTHORITY_AUTHORITY_ID_FK
        FOREIGN KEY (AUTHORITY_ID) REFERENCES AK_AUTHORITY (ID)
            ON
                DELETE CASCADE
            ON
                UPDATE CASCADE
);


CREATE TABLE AK_USER_IMAGE
(
    ID         INT AUTO_INCREMENT PRIMARY KEY,
    USER_ID    INT NOT NULL,
    IMAGE_NAME VARCHAR(255),
    POSITION   INT NOT NULL,
    CONSTRAINT USER_IMAGE_USER_ID_POSITION_U UNIQUE (USER_ID, POSITION),
    CONSTRAINT USER_IMAGE_USER_ID_FK
        FOREIGN KEY (USER_ID) REFERENCES AK_USER (ID)
            ON
                DELETE CASCADE
            ON
                UPDATE CASCADE
);

CREATE TABLE AK_NOTIFICATION
(
    ID          INT AUTO_INCREMENT PRIMARY KEY,
    TITLE       TINYTEXT                            NOT NULL,
    DESCRIPTION TEXT,
    CREATED_AT  TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    UPDATED_AT  TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
);

CREATE TABLE AK_CHAT
(
    ID         INT AUTO_INCREMENT PRIMARY KEY,
    TITLE      VARCHAR(255),
    CREATED_AT TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    UPDATED_AT TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
);

CREATE TABLE AK_CHAT_USER
(
    ID         INT AUTO_INCREMENT PRIMARY KEY,
    CHAT_ID    INT                                 NOT NULL,
    USER_ID    INT                                 NOT NULL,
    CREATED_AT TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    UPDATED_AT TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT CHAT_USER_CHAT_ID_USER_ID_U UNIQUE (CHAT_ID, USER_ID),
    CONSTRAINT CHAT_USER_USER_ID_FK
        FOREIGN KEY (USER_ID) REFERENCES AK_USER (ID)
            ON
                DELETE CASCADE
            ON
                UPDATE CASCADE,
    CONSTRAINT CHAT_USER_CHAT_ID_FK
        FOREIGN KEY (CHAT_ID) REFERENCES AK_CHAT (ID)
            ON
                DELETE CASCADE
            ON
                UPDATE CASCADE
);


CREATE TABLE AK_CHAT_LINE
(
    ID           INT AUTO_INCREMENT PRIMARY KEY,
    CHAT_USER_ID INT          NOT NULL,
    CONTENT      TEXT         NOT NULL,
    TYPE         INT          NOT NULL DEFAULT 0,
    READ_BY      VARCHAR(255) NOT NULL DEFAULT '[]',
    CREATED_AT   TIMESTAMP             DEFAULT CURRENT_TIMESTAMP NOT NULL,
    UPDATED_AT   TIMESTAMP             DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT CHAT_LINE_USER_ID_FK
        FOREIGN KEY (CHAT_USER_ID) REFERENCES AK_CHAT_USER (ID)
            ON
                DELETE CASCADE
            ON
                UPDATE CASCADE
);

-- auth 2.0

DROP TABLE IF EXISTS oauth_client_details;
DROP TABLE IF EXISTS oauth_client_token;
DROP TABLE IF EXISTS oauth_access_token;
DROP TABLE IF EXISTS oauth_refresh_token;
DROP TABLE IF EXISTS oauth_code;
DROP TABLE IF EXISTS oauth_approvals;



create table oauth_client_details
(
    client_id               varchar(255) not null,
    client_secret           varchar(255) not null,
    web_server_redirect_uri varchar(2048) default null,
    scope                   varchar(255)  default null,
    access_token_validity   int(11)       default null,
    refresh_token_validity  int(11)       default null,
    resource_ids            varchar(1024) default null,
    authorized_grant_types  varchar(1024) default null,
    authorities             varchar(1024) default null,
    additional_information  varchar(4096) default null,
    autoapprove             varchar(255)  default null,
    primary key (client_id)
);

create table oauth_client_token
(
    token_id          VARCHAR(256),
    token             VARBINARY,
    authentication_id VARCHAR(256) PRIMARY KEY,
    user_name         VARCHAR(256),
    client_id         VARCHAR(256)
);
create table oauth_access_token
(
    token_id          VARCHAR(256),
    token             VARBINARY,
    authentication_id VARCHAR(256) PRIMARY KEY,
    user_name         VARCHAR(256),
    client_id         VARCHAR(256),
    authentication    VARBINARY,
    refresh_token     VARCHAR(256)
);

create table oauth_refresh_token
(
    token_id       VARCHAR(256),
    token          VARBINARY,
    authentication VARBINARY
);

create table oauth_code
(
    code           VARCHAR(256),
    authentication VARBINARY
);

create table oauth_approvals
(
    userId         VARCHAR(256),
    clientId       VARCHAR(256),
    scope          VARCHAR(256),
    status         VARCHAR(10),
    expiresAt      TIMESTAMP,
    lastModifiedAt TIMESTAMP
);