package com.template.webserver.controllers

import com.template.domain.TrxReference
import com.template.webserver.constants.enums.ResponseCode
import com.template.webserver.constants.enums.ResponseStatus
import com.template.webserver.model.rest.request.ValidateConsumedStateRequest
import com.template.webserver.model.rest.response.CommonResponse
import com.template.webserver.services.state.StateService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/state")
class StateController(private val service: StateService) : AbstractBaseController() {

    @PostMapping("/checkStateStatusByTrxReference")
    fun checkStateStatusByTrxReference(@RequestBody request: ValidateConsumedStateRequest) : CommonResponse<TrxReference> {
        return service.inquiryStateStatusByTrxReference(request.trxReference)
    }

}