package com.icebreaker.be.db.repository

import com.icebreaker.be.db.entity.AkChatUserEntity
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ChatUserRepository : CrudRepository<AkChatUserEntity, Int> {
    fun findByChatIdAndUserId(chatId: Int, userId: Int): AkChatUserEntity?
    @Query(nativeQuery = true, value = "SELECT * FROM (SELECT GROUP_CONCAT(USER_ID) AS idKey, CHAT_ID FROM (SELECT * FROM AK_CHAT_USER ORDER BY CHAT_ID, USER_ID) as `ACU*` GROUP BY CHAT_ID) as iKCI WHERE idKey = :userIds")
    fun findByUserIds(userIds: String): AkChatUserEntity?

}