package com.icebreaker.be.db.repository

import com.icebreaker.be.db.entity.AkUserEntity
import com.icebreaker.be.db.entity.AkUserPositionEntity
import org.springframework.data.repository.CrudRepository

interface UserPositionRepository : CrudRepository<AkUserPositionEntity, Int> {
}