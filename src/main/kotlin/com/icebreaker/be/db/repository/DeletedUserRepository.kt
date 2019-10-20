package com.icebreaker.be.db.repository

import com.icebreaker.be.db.entity.AkDeletedUserEntity
import org.springframework.data.repository.CrudRepository

interface DeletedUserRepository : CrudRepository<AkDeletedUserEntity, Int>