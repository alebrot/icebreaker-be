package com.icebreaker.be

import com.icebreaker.be.controller.user.dto.ProductDto
import com.icebreaker.be.exception.CreditsNotAvailableException
import com.icebreaker.be.exception.FileNotFoundException
import com.icebreaker.be.service.model.toDto
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler


@ControllerAdvice
class RestExceptionHandler : ResponseEntityExceptionHandler() {

    val log: Logger = LoggerFactory.getLogger(RestExceptionHandler::class.java)

    @ExceptionHandler(CreditsNotAvailableException::class)
    fun handleCreditsNotAvailableException(
            ex: CreditsNotAvailableException): ResponseEntity<Any> {
        val products = ex.products.map { it.toDto() }
        val creditsNotAvailable = CreditsNotAvailableError("${ex.localizedMessage}, required credits: ${ex.requiredCredits}", products)
        return ResponseEntity(creditsNotAvailable, HttpStatus.NOT_ACCEPTABLE)
    }

    @ExceptionHandler(FileNotFoundException::class)
    fun handleFileNotFoundException(ex: FileNotFoundException): ResponseEntity<Any> {
        log(ex, true)
        return ResponseEntity(ex.message, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(ex: IllegalArgumentException): ResponseEntity<Any> {
        log(ex)
        return ResponseEntity(ex.message, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(Exception::class)
    fun handleException(ex: Exception): ResponseEntity<Any> {
        log(ex)
        return ResponseEntity(ex.message, HttpStatus.INTERNAL_SERVER_ERROR)
    }

    private fun log(ex: Exception, suppressStacktrace: Boolean = false) {
        if (suppressStacktrace) {
            log.error(ex.message)
        } else {
            log.error(ex.message, ex)
        }
    }
}


data class CreditsNotAvailableError(val error: String, val products: List<ProductDto>)