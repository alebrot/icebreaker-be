package com.icebreaker.be.db.repository

import com.icebreaker.be.db.entity.AkUserEntity
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param

interface UserRepository : CrudRepository<AkUserEntity, Int> {
    fun findByEmail(email: String): AkUserEntity?

    @Query(value = "SELECT * FROM (SELECT (6371 * acos( cos(radians(LAT)) * cos(radians(LAT_ORIGINAL)) * cos(radians(LON_ORIGINAL) - radians(LON)) + sin(radians(LAT)) * sin(radians(LAT_ORIGINAL)) )) * 1000 as distance, A.* FROM (SELECT LAT AS LAT_ORIGINAL, LON AS LON_ORIGINAL FROM AK_POSITION INNER JOIN AK_USER AU on AK_POSITION.ID = AU.POSITION_ID WHERE AU.ID = :userId) as LOLO INNER JOIN AK_POSITION INNER JOIN AK_USER A on AK_POSITION.ID = A.POSITION_ID WHERE A.ID != :userId) as `d*` WHERE distance < :distanceInMeters", nativeQuery = true)
    fun findUsersCloseToUser(@Param("userId") userId: Int, @Param("distanceInMeters") distanceInMeters: Int): List<Map<String, Any>>

    @Query(value = "SELECT * FROM (SELECT (6371 * acos( cos(radians(LAT)) * cos(radians( :latOriginal)) * cos(radians( :lonOriginal) - radians(LON)) + sin(radians(LAT)) * sin(radians(:latOriginal)) )) * 1000 as distance, A.* FROM AK_POSITION INNER JOIN AK_USER A on AK_POSITION.ID = A.POSITION_ID WHERE A.ID != :userId) as `d*` WHERE distance < :distanceInMeters", nativeQuery = true)
    fun findUsersCloseToUserPosition(@Param("userId") userId: Int, @Param("distanceInMeters") distanceInMeters: Int, @Param("latOriginal") latOriginal: Double, @Param("lonOriginal") lonOriginal: Double): List<Map<String, Any>>
}