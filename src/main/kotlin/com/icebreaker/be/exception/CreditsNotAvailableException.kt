package com.icebreaker.be.exception

import com.icebreaker.be.service.model.Product

class CreditsNotAvailableException(
        val products: List<Product>,
        override val message: String,
        val requiredCredits: Int?
) : IllegalStateException(message) {
}