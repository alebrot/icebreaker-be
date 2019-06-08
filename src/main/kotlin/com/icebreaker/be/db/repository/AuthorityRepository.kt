package com.icebreaker.be.db.repository

import com.icebreaker.be.db.entity.AkAuthorityEntity
import org.springframework.data.repository.CrudRepository

interface AuthorityRepository : CrudRepository<AkAuthorityEntity, Int>