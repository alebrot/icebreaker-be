package com.icebreaker.be.db.repository

import com.icebreaker.be.db.entity.AkChatEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ChatRepository : CrudRepository<AkChatEntity, Int>