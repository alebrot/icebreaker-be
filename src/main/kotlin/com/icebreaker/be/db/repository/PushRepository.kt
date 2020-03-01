package com.icebreaker.be.db.repository

import com.icebreaker.be.db.entity.AkPushEntity
import org.springframework.data.repository.CrudRepository

interface PushRepository : CrudRepository<AkPushEntity, Int> {
    fun deleteByUserId(userId: String): List<AkPushEntity>
}