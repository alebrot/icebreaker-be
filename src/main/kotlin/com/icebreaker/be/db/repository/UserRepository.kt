package com.icebreaker.be.db.repository

import com.icebreaker.be.db.entity.AkUserEntity
import org.springframework.data.repository.CrudRepository

interface UserRepository : CrudRepository<AkUserEntity, Int> {
    fun findByEmail(email: String): AkUserEntity?
}