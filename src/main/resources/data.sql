INSERT INTO AK_USER (ID, EMAIL, PASSWORD_HASH, FIRST_NAME, LAST_NAME)
VALUES (1, 'email1@email.com', /*password*/'$2a$08$EfoMXblxEFhBxSCOxI/bju0G4oVwGgtCig6INKKOJbchvNw5XhoTW',
        'FirstName 1', 'LastName 1');
INSERT INTO AK_USER (ID, EMAIL, PASSWORD_HASH, FIRST_NAME, LAST_NAME)
VALUES (2, 'email2@email.com', /*password*/'$2a$08$EfoMXblxEFhBxSCOxI/bju0G4oVwGgtCig6INKKOJbchvNw5XhoTW',
        'FirstName 2', 'LastName 2');

INSERT INTO AK_AUTHORITY(ID, NAME)
VALUES (1, 'USER');
INSERT INTO AK_AUTHORITY(ID, NAME)
VALUES (2, 'ADMIN');

INSERT INTO AK_USER_AUTHORITY(ID, USER_ID, AUTHORITY_ID)
VALUES (1, 1, 1);
INSERT INTO AK_USER_AUTHORITY(ID, USER_ID, AUTHORITY_ID)
VALUES (2, 1, 2);


INSERT INTO OAUTH_CLIENT_DETAILS(CLIENT_ID, RESOURCE_IDS, CLIENT_SECRET, SCOPE, AUTHORIZED_GRANT_TYPES, AUTHORITIES,
                                 ACCESS_TOKEN_VALIDITY, REFRESH_TOKEN_VALIDITY)

VALUES ('spring-security-oauth2-read-client', 'resource-server-rest-api',

           /*spring-security-oauth2-read-client-password1234*/'$2a$04$WGq2P9egiOYoOFemBRfsiO9qTcyJtNRnPKNBl5tokP7IP.eZn93km',
        'read', 'password,authorization_code,refresh_token,implicit', 'USER', 10800, 2592000);

INSERT INTO OAUTH_CLIENT_DETAILS(CLIENT_ID, RESOURCE_IDS, CLIENT_SECRET, SCOPE, AUTHORIZED_GRANT_TYPES, AUTHORITIES,
                                 ACCESS_TOKEN_VALIDITY, REFRESH_TOKEN_VALIDITY)

VALUES ('spring-security-oauth2-read-write-client', 'resource-server-rest-api',

           /*spring-security-oauth2-read-write-client-password1234*/'$2a$04$soeOR.QFmClXeFIrhJVLWOQxfHjsJLSpWrU1iGxcMGdu.a5hvfY4W',
        'read,write', 'password,authorization_code,refresh_token,implicit', 'USER', 10800, 2592000);

