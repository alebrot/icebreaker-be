package com.icebreaker.be.db.repository

import com.icebreaker.be.db.entity.AkSocialEntity
import com.icebreaker.be.db.entity.AkUserEntity
import com.icebreaker.be.service.auth.social.SocialType
import org.springframework.data.repository.CrudRepository

interface SocialRepository : CrudRepository<AkSocialEntity, Int> {
    fun findBySocialIdAndType(email: String, type: SocialType): AkSocialEntity?
}