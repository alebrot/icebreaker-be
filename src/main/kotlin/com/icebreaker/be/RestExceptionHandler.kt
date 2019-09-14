package com.icebreaker.be

import com.icebreaker.be.controller.user.dto.ProductDto
import com.icebreaker.be.exception.CreditsNotAvailableException
import com.icebreaker.be.service.model.toDto
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler


@ControllerAdvice
class RestExceptionHandler : ResponseEntityExceptionHandler() {
    @ExceptionHandler(CreditsNotAvailableException::class)
    fun handleCreditsNotAvailableException(
            ex: CreditsNotAvailableException): ResponseEntity<Any> {

        val products = ex.products.map { it.toDto() }
        val creditsNotAvailable = CreditsNotAvailableError("${ex.localizedMessage}, required credits: ${ex.requiredCredits}", products)
        return ResponseEntity(creditsNotAvailable, HttpStatus.NOT_ACCEPTABLE)
    }
}


data class CreditsNotAvailableError(val error: String, val products: List<ProductDto>)