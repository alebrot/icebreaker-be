package com.icebreaker.be.db.repository

import com.icebreaker.be.db.entity.AkChatEntity
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface ChatRepository : CrudRepository<AkChatEntity, Int> {
    @Query(nativeQuery = true, value = "SELECT * FROM (SELECT *FROM (SELECT GROUP_CONCAT(USER_ID) AS idKey, CHAT_ID FROM (SELECT * FROM AK_CHAT_USER ORDER BY CHAT_ID, USER_ID) as `ACU*` GROUP BY CHAT_ID) as iKCI WHERE idKey = :userIds) AS T LEFT JOIN AK_CHAT ON T.CHAT_ID = AK_CHAT.ID LIMIT 1")
    fun findByUserIds(userIds: String): AkChatEntity?

    @Query(value = "SELECT AK_CHAT.ID, AK_CHAT.TITLE, AK_CHAT.CREATED_AT, AK_CHAT.UPDATED_AT, ACU.ENABLED FROM AK_CHAT INNER JOIN AK_CHAT_USER ACU on AK_CHAT.ID = ACU.CHAT_ID WHERE USER_ID = :userId", nativeQuery = true)
    fun findChatsByUserId(@Param("userId") userId: Int): List<Map<String, Any>>
}