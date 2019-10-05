package com.icebreaker.be.db.repository

import com.icebreaker.be.db.entity.AkProductEntity
import com.icebreaker.be.service.model.Store
import org.springframework.data.repository.CrudRepository

interface ProductRepository : CrudRepository<AkProductEntity, Int> {
    fun findAllByStoreOrderById(store: Store): List<AkProductEntity>
    fun findByProductId(productId: String): AkProductEntity?
}