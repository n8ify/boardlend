package com.template.webserver.model.rest.response

import com.template.webserver.constants.enums.ResponseCode
import com.template.webserver.constants.enums.ResponseStatus
import java.time.Instant

interface AbstractBaseResponse<T> {

    val status: ResponseStatus
    val code: ResponseCode
    val message: String?
    val timestamp: Instant
    val data: T?

}