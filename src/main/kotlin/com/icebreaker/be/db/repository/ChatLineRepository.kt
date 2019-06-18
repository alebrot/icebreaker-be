package com.icebreaker.be.db.repository

import com.icebreaker.be.db.entity.AkChatLineEntity
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ChatLineRepository : CrudRepository<AkChatLineEntity, Int> {
    @Query(nativeQuery = true, value = "SELECT * FROM AK_CHAT_LINE INNER JOIN AK_CHAT_USER ACU on AK_CHAT_LINE.CHAT_USER_ID = ACU.ID WHERE CHAT_ID = :chatId ORDER BY AK_CHAT_LINE.CREATED_AT DESC LIMIT :limit OFFSET :offset")
    fun findByChatId(chatId: Int, limit: Int, offset: Int): List<AkChatLineEntity>
}
