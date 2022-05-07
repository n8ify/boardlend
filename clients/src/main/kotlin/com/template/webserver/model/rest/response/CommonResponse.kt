package com.template.webserver.model.rest.response

import com.template.webserver.constants.enums.ResponseCode
import com.template.webserver.constants.enums.ResponseStatus
import java.time.Instant

open class CommonResponse<T>(
    override val status: ResponseStatus,
    override val code: ResponseCode,
    override val message: String? = null,
    override val timestamp: Instant = Instant.now(),
    override val data: T? = null
) : AbstractBaseResponse<T>
