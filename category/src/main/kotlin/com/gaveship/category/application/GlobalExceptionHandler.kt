package com.gaveship.category.application

import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.ServletWebRequest

@RestControllerAdvice
class GlobalExceptionHandler {
    companion object {
        private val log = KotlinLogging.logger { }
    }

    @ExceptionHandler(value = [Exception::class])
    private fun handleException(exception: Exception, servletWebRequest: ServletWebRequest): ResponseEntity<ErrorMessage> {
        log.error(exception) { exception.message }
        val httpStatus = HttpStatus.INTERNAL_SERVER_ERROR
        return ResponseEntity.status(httpStatus).body(
            ErrorMessage(
                status = httpStatus.value(),
                error = httpStatus.name,
                path = servletWebRequest.request.requestURI
            )
        )
    }

    @ExceptionHandler(value = [BindException::class, IllegalArgumentException::class, NotExistCategoryException::class])
    private fun handleCustomException(exception: Exception, servletWebRequest: ServletWebRequest): ResponseEntity<ErrorMessage> {
        log.error(exception) { exception.message }
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