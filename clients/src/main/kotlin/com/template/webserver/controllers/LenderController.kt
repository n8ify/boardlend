package com.template.webserver.controllers

import com.template.states.LenderState
import com.template.webserver.model.rest.request.lender.CreateLenderAccountRequest
import com.template.webserver.model.rest.response.CommonResponse
import com.template.webserver.services.lender.LenderService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/lender")
class LenderController(private val lenderService: LenderService) : AbstractBaseController() {

    @PostMapping("/create")
    fun create(@RequestBody request: CreateLenderAccountRequest): CommonResponse<LenderState.StateData> {
        return lenderService.create(request)
    }

}