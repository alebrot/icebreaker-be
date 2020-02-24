package com.icebreaker.be.service.credit

data class Receipt(val orderId: String, val packageName: String, val productId: String, val purchaseTime: Long, val purchaseState: Int, val purchaseToken: String)
