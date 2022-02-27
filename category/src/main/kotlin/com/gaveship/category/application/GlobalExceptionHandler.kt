package com.gaveship.category.application

import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.ServletWebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@RestControllerAdvice
class GlobalExceptionHandler : ResponseEntityExceptionHandler() {
    companion object {
        private val log = KotlinLogging.logger { }
    }

    @ExceptionHandler(value = [IllegalArgumentException::class, NotExistCategoryException::class])
    private fun handleIllegalArgumentException(exception: Exception, servletWebRequest: ServletWebRequest): ResponseEntity<ErrorMessage> {
        log.error { exception }
        val httpStatus = HttpStatus.BAD_REQUEST
        return ResponseEntity.status(httpStatus).body(
            ErrorMessage(
                status = httpStatus.value(),
                error = httpStatus.name,
                path = servletWebRequest.request.requestURI
            )
        )
    }
}