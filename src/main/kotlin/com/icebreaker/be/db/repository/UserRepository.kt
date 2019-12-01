package com.icebreaker.be.db.repository

import com.icebreaker.be.db.entity.AkUserEntity
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param

interface UserRepository : CrudRepository<AkUserEntity, Int> {
    fun findByEmail(email: String): AkUserEntity?

    @Query(value = "SELECT *FROM AK_USER WHERE EMAIL LIKE CONCAT('%',:email,'%') LIMIT :limit OFFSET :offset", nativeQuery = true)
    fun findAllByEmailContaining(@Param("email") email: String, @Param("limit") limit: Int, @Param("offset") offset: Int): List<AkUserEntity>

    @Query(value = "SELECT * FROM (SELECT (6371 * acos( cos(radians(LAT)) * cos(radians(LAT_ORIGINAL)) * cos(radians(LON_ORIGINAL) - radians(LON)) + sin(radians(LAT)) * sin(radians(LAT_ORIGINAL)) )) * 1000 as distance, A.* FROM (SELECT LAT AS LAT_ORIGINAL, LON AS LON_ORIGINAL FROM AK_POSITION INNER JOIN AK_USER AU on AK_POSITION.ID = AU.POSITION_ID WHERE AU.ID = :userId) as LOLO INNER JOIN AK_POSITION INNER JOIN AK_USER A on AK_POSITION.ID = A.POSITION_ID WHERE A.ID != :userId) as `d*` WHERE distance < :distanceInMeters LIMIT :limit OFFSET :offset", nativeQuery = true)
    fun findUsersCloseToUser(@Param("userId") userId: Int, @Param("distanceInMeters") distanceInMeters: Int, @Param("limit") limit: Int, @Param("offset") offset: Int): List<Map<String, Any>>

    @Query(value = "SELECT * FROM (SELECT (6371 * acos( cos(radians(LAT)) * cos(radians( :latOriginal)) * cos(radians( :lonOriginal) - radians(LON)) + sin(radians(LAT)) * sin(radians(:latOriginal)) )) * 1000 as distance, A.* FROM AK_POSITION INNER JOIN AK_USER A on AK_POSITION.ID = A.POSITION_ID WHERE A.ID != :userId) as `d*` WHERE distance < :distanceInMeters LIMIT :limit OFFSET :offset", nativeQuery = true)
    fun findUsersCloseToUserPosition(@Param("userId") userId: Int, @Param("distanceInMeters") distanceInMeters: Int, @Param("latOriginal") latOriginal: Double, @Param("lonOriginal") lonOriginal: Double, @Param("limit") limit: Int, @Param("offset") offset: Int): List<Map<String, Any>>

    @Query(value = "SELECT * FROM (SELECT (6371 * acos(cos(radians(LAT))  * cos(radians(LAT_ORIGINAL))  * cos(radians(LON_ORIGINAL) - radians(LON)) + sin(radians(LAT)) * sin(radians(LAT_ORIGINAL)))) * 1000 as distance FROM (SELECT LAT AS LAT_ORIGINAL, LON AS LON_ORIGINAL FROM AK_POSITION INNER JOIN AK_USER AU on AK_POSITION.ID = AU.POSITION_ID  WHERE AU.ID = :userId1) as LOLO INNER JOIN AK_POSITION INNER JOIN AK_USER A on AK_POSITION.ID = A.POSITION_ID  WHERE A.ID = :userId2) as LOLOAPAd", nativeQuery = true)
    fun findDistanceBetweenUsers(@Param("userId1") userId1: Int, @Param("userId2") userId2: Int): Int

    @Query(value = "SELECT * FROM (SELECT * FROM (SELECT *  FROM (SELECT (6371 *  acos(cos(radians(LAT)) * cos(radians(LAT_ORIGINAL)) *                         cos(radians(LON_ORIGINAL) - radians(LON)) +   sin(radians(LAT)) * sin(radians(LAT_ORIGINAL)))) * 1000 as distance,  A.* FROM (SELECT LAT AS LAT_ORIGINAL, LON AS LON_ORIGINAL FROM AK_POSITION  INNER JOIN AK_USER AU on AK_POSITION.ID = AU.POSITION_ID WHERE AU.ID = :userId) as LOLO  INNER JOIN AK_POSITION INNER JOIN AK_USER A on AK_POSITION.ID = A.POSITION_ID  WHERE A.ID != :userId) as `d*` WHERE distance < :distanceInMeters) C UNION ALL SELECT FLOOR(RAND() * (:distanceInMeters - 1) + 1) as distance, B.*FROM AK_USER B WHERE ID < 120 AND ID != :userId) D GROUP BY ID, distance, FIRST_NAME, LAST_NAME, EMAIL, PASSWORD_HASH, IMG_URL, BIRTHDAY, BIO, GENDER, CREATED_AT, UPDATED_AT, ACCOUNT_EXPIRED, ACCOUNT_LOCKED, CREDENTIALS_EXPIRED, ENABLED, POSITION_ID, PUSH_ID, LAST_SEEN, CREDITS, CREDITS_UPDATED_AT, ADMOB_COUNT, ADMOB_UPDATED_AT, INVITED_BY ORDER BY ID LIMIT :limit OFFSET :offset", nativeQuery = true)
    fun findUsersCloseToUserWithFakeUsers(@Param("userId") userId: Int, @Param("distanceInMeters") distanceInMeters: Int, @Param("limit") limit: Int, @Param("offset") offset: Int): List<Map<String, Any>>

    @Query(value = "SELECT * FROM (SELECT * FROM (SELECT (6371 * acos(cos(radians(LAT)) * cos(radians(:latOriginal)) * cos(radians(:lonOriginal) - radians(LON)) +  sin(radians(LAT)) * sin(radians(:latOriginal)))) * 1000 as distance, A.* FROM AK_POSITION INNER JOIN AK_USER A on AK_POSITION.ID = A.POSITION_ID  WHERE A.ID != :userId) as `d*` WHERE distance < :distanceInMeters UNION ALL SELECT FLOOR(RAND() * (:distanceInMeters - 1) + 1) as distance, B.*FROM AK_USER B WHERE ID < 120 AND ID != :userId ) F GROUP BY ID, distance, FIRST_NAME, LAST_NAME, EMAIL, PASSWORD_HASH, IMG_URL, BIRTHDAY, BIO, GENDER, CREATED_AT, UPDATED_AT, ACCOUNT_EXPIRED, ACCOUNT_LOCKED, CREDENTIALS_EXPIRED, ENABLED, POSITION_ID, PUSH_ID, LAST_SEEN, CREDITS, CREDITS_UPDATED_AT, ADMOB_COUNT, ADMOB_UPDATED_AT, INVITED_BY ORDER BY ID LIMIT :limit OFFSET :offset", nativeQuery = true)
    fun findUsersCloseToUserPositionWithFakeUsers(@Param("userId") userId: Int, @Param("distanceInMeters") distanceInMeters: Int, @Param("latOriginal") latOriginal: Double, @Param("lonOriginal") lonOriginal: Double, @Param("limit") limit: Int, @Param("offset") offset: Int): List<Map<String, Any>>

    @Query(value = "SELECT COUNT(AK_USER.ID) AS COUNT, AK_USER.CREDITS AS CREDITS  FROM AK_USER GROUP BY AK_USER.CREDITS ORDER BY AK_USER.CREDITS DESC LIMIT :limit OFFSET :offset", nativeQuery = true)
    fun countUsersByAvailablePoints(@Param("limit") limit: Int, @Param("offset") offset: Int): List<Map<String, Int>>

    @Query(value = "SELECT COUNT(DATE(LAST_SEEN)) AS COUNT, DATE(LAST_SEEN) AS DATE FROM AK_USER GROUP BY DATE(LAST_SEEN) ORDER BY DATE(LAST_SEEN) DESC LIMIT :limit OFFSET :offset", nativeQuery = true)
    fun countOnlineUsersByDate(@Param("limit") limit: Int, @Param("offset") offset: Int): List<Map<String, Any>>

}