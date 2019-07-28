package com.icebreaker.be.exception

class CreditsNotAvailableException(override val message: String, val requiredCredits: Int?) : IllegalStateException(message) {
}