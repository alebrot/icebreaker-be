package com.icebreaker.be.db.repository

import com.icebreaker.be.db.entity.AkUserImageEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface UserImageRepository : CrudRepository<AkUserImageEntity, Int> {
    fun findByUserIdOrderByPosition(userId: Int): List<AkUserImageEntity>
    fun findByUserIdAndPosition(userId: Int, position: Int): AkUserImageEntity?
}

