package com.icebreaker.be.db.repository

import com.icebreaker.be.db.entity.AkCreditLogEntity
import org.springframework.data.repository.CrudRepository

interface CreditLogRepository : CrudRepository<AkCreditLogEntity, Int>