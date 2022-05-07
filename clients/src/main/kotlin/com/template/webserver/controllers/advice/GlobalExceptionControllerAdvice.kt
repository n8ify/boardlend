package com.template.webserver.controllers.advice

import com.template.webserver.constants.enums.ResponseCode
import com.template.webserver.model.rest.response.ErrorResponse
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionControllerAdvice {

    @ExceptionHandler(value = [Exception::class])
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun handlerUncaughtException(ex: Exception): ErrorResponse {
        return ErrorResponse(
            code = ResponseCode.E00000,
            errorMessage = ex.message
        )
    }

}