package com.icebreaker.be.db.repository

import com.icebreaker.be.db.entity.AkPushEntity
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository

interface PushRepository : CrudRepository<AkPushEntity, Int> {
    @Query("SELECT * FROM AK_PUSH INNER JOIN AK_USER AU on AK_PUSH.ID = AU.PUSH_ID WHERE AK_PUSH.USER_ID = :userId AND AU.ID != :myUserId", nativeQuery = true)
    fun findPushUserIdForAnotherUsers(userId: String, myUserId: Int): List<AkPushEntity>
}