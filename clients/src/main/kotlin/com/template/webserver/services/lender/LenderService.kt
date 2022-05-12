package com.template.webserver.services.lender

import com.template.states.LenderState
import com.template.webserver.model.rest.request.lender.CreateLenderAccountRequest
import com.template.webserver.model.rest.response.CommonResponse

interface LenderService {

    fun create(request: CreateLenderAccountRequest): CommonResponse<LenderState.StateData>

}