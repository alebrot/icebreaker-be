package com.icebreaker.be.db.repository

import com.icebreaker.be.db.entity.AkChatUserEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ChatUserRepository : CrudRepository<AkChatUserEntity, Int> {
    fun findByChatIdAndUserId(chatId: Int, userId: Int): AkChatUserEntity?
}