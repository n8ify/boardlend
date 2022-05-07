package com.template.webserver.model.rest.response

import com.template.webserver.constants.enums.ResponseCode
import com.template.webserver.constants.enums.ResponseStatus
import java.time.Instant

data class ErrorResponse(
    override val status: ResponseStatus = ResponseStatus.Error,
    override val code: ResponseCode,
    private val errorMessage: String?,
    override val timestamp: Instant = Instant.now(),
    override val data: Unit? = null
) : AbstractBaseResponse<Unit> {

    override val message: String
            get() = errorMessage ?: code.description
}