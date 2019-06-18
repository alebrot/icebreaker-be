INSERT INTO AK_POSITION (ID, LAT, LON)
VALUES (1, 45.4750572, 9.1748737);
INSERT INTO AK_POSITION (ID, LAT, LON)
VALUES (2, 45.4748338, 9.1746082);
INSERT INTO AK_POSITION (ID, LAT, LON)
VALUES (3, 45.5748338, 9.1746082);


INSERT INTO AK_USER (ID, POSITION_ID, EMAIL, PASSWORD_HASH, FIRST_NAME, LAST_NAME)
VALUES (1, 1, 'email1@email.com', /*password*/'$2a$08$EfoMXblxEFhBxSCOxI/bju0G4oVwGgtCig6INKKOJbchvNw5XhoTW',
        'FirstName 1', 'LastName 1');
INSERT INTO AK_USER (ID, POSITION_ID, EMAIL, PASSWORD_HASH, FIRST_NAME, LAST_NAME)
VALUES (2, 2, 'email2@email.com', /*password*/'$2a$08$EfoMXblxEFhBxSCOxI/bju0G4oVwGgtCig6INKKOJbchvNw5XhoTW',
        'FirstName 2', 'LastName 2');
INSERT INTO AK_USER (ID, POSITION_ID, EMAIL, PASSWORD_HASH, FIRST_NAME, LAST_NAME)
VALUES (3, 3, 'email3@email.com', /*password*/'$2a$08$EfoMXblxEFhBxSCOxI/bju0G4oVwGgtCig6INKKOJbchvNw5XhoTW',
        'FirstName 3', 'LastName 3');


INSERT INTO AK_AUTHORITY(ID, NAME)
VALUES (1, 'USER');
INSERT INTO AK_AUTHORITY(ID, NAME)
VALUES (2, 'ADMIN');

INSERT INTO AK_USER_AUTHORITY(ID, USER_ID, AUTHORITY_ID)
VALUES (1, 1, 1);
INSERT INTO AK_USER_AUTHORITY(ID, USER_ID, AUTHORITY_ID)
VALUES (2, 1, 2);
INSERT INTO AK_USER_AUTHORITY(ID, USER_ID, AUTHORITY_ID)
VALUES (3, 2, 1);

INSERT INTO AK_USER_IMAGE (ID, USER_ID, IMAGE_NAME, POSITION)
VALUES (1, 1, 'Image 1.png', 1);
INSERT INTO AK_USER_IMAGE (ID, USER_ID, IMAGE_NAME, POSITION)
VALUES (2, 1, 'Image 2.png', 2);


INSERT INTO AK_CHAT(ID, TITLE)
VALUES (1, 'Chat 1');

INSERT INTO AK_CHAT_USER(ID, CHAT_ID, USER_ID)
VALUES (1, 1, 1);
INSERT INTO AK_CHAT_USER(ID, CHAT_ID, USER_ID)
VALUES (2, 1, 2);

INSERT INTO AK_CHAT_LINE(ID, CHAT_USER_ID, CONTENT, CREATED_AT)
VALUES (1, 1, 'Message 1 from user 1 to chat 1', '2019-06-18 08:33:22.556000000');

INSERT INTO AK_CHAT_LINE(ID, CHAT_USER_ID, CONTENT, CREATED_AT)
VALUES (2, 1, 'Message 2 from user 1 to chat 1', '2019-06-18 09:33:22.556000000');

INSERT INTO AK_CHAT_LINE(ID, CHAT_USER_ID, CONTENT, CREATED_AT)
VALUES (3, 2, 'Message 1 from user 2 to chat 1', '2019-06-18 10:33:22.556000000');

INSERT INTO AK_CHAT_LINE(ID, CHAT_USER_ID, CONTENT, CREATED_AT)
VALUES (4, 1, 'Message 1 from user 1 to chat 1', '2019-06-18 11:33:22.556000000');

INSERT INTO OAUTH_CLIENT_DETAILS(CLIENT_ID, RESOURCE_IDS, CLIENT_SECRET, SCOPE, AUTHORIZED_GRANT_TYPES, AUTHORITIES,
                                 ACCESS_TOKEN_VALIDITY, REFRESH_TOKEN_VALIDITY)

VALUES ('spring-security-oauth2-read-client', 'resource-server-rest-api',

           /*spring-security-oauth2-read-client-password1234*/'$2a$04$WGq2P9egiOYoOFemBRfsiO9qTcyJtNRnPKNBl5tokP7IP.eZn93km',
        'read', 'password,authorization_code,refresh_token,implicit,social', 'USER', 10800, 2592000);

INSERT INTO OAUTH_CLIENT_DETAILS(CLIENT_ID, RESOURCE_IDS, CLIENT_SECRET, SCOPE, AUTHORIZED_GRANT_TYPES, AUTHORITIES,
                                 ACCESS_TOKEN_VALIDITY, REFRESH_TOKEN_VALIDITY)

VALUES ('spring-security-oauth2-read-write-client', 'resource-server-rest-api',

           /*spring-security-oauth2-read-write-client-password1234*/'$2a$04$soeOR.QFmClXeFIrhJVLWOQxfHjsJLSpWrU1iGxcMGdu.a5hvfY4W',
        'read,write', 'password,authorization_code,refresh_token,implicit,social', 'USER', 10800, 2592000);


INSERT INTO OAUTH_CLIENT_DETAILS(CLIENT_ID, RESOURCE_IDS, CLIENT_SECRET, SCOPE, AUTHORIZED_GRANT_TYPES, AUTHORITIES,
                                 ACCESS_TOKEN_VALIDITY, REFRESH_TOKEN_VALIDITY)

VALUES ('spring-security-oauth2-read-write-client-fb', 'resource-server-rest-api',

           /*spring-security-oauth2-read-write-client-password1234*/'$2a$04$soeOR.QFmClXeFIrhJVLWOQxfHjsJLSpWrU1iGxcMGdu.a5hvfY4W',
        'read,write', 'password,authorization_code,refresh_token,implicit,social', 'USER', 10800, 2592000);


-- SELECT *
-- FROM (SELECT (6371 * acos(
--                 cos(radians(LAT))
--                 * cos(radians(LAT_ORIGINAL))
--                 * cos(radians(LON_ORIGINAL) - radians(LON))
--             + sin(radians(LAT))
--                     * sin(radians(LAT_ORIGINAL))
--     )) * 1000 as distance,
--              A.*
--       FROM (SELECT LAT AS LAT_ORIGINAL, LON AS LON_ORIGINAL
--             FROM AK_POSITION
--                      INNER JOIN AK_USER AU on AK_POSITION.ID = AU.POSITION_ID
--             WHERE AU.ID = 1)
--                INNER JOIN AK_POSITION
--                INNER JOIN AK_USER A on AK_POSITION.ID = A.POSITION_ID
--       WHERE A.ID != 1)
-- WHERE distance < 1000
