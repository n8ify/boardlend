package com.template.webserver.model.rest.request

import com.template.domain.TrxReference

data class ValidateConsumedStateRequest(val trxReference: TrxReference) {
    constructor(): this(TrxReference(0, ""))
}