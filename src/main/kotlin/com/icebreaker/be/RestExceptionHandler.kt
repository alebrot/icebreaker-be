package com.icebreaker.be

import com.icebreaker.be.exception.CreditsNotAvailableException
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
        return ResponseEntity(ex, HttpStatus.NOT_ACCEPTABLE)
    }
}