package com.template.webserver.controllers

import com.template.domain.RepositoryQueryParams
import com.template.domain.PaginatedResponse
import com.template.schemas.BorrowerSchemaV1
import com.template.states.BorrowerState
import com.template.webserver.model.rest.request.borrower.CreateBorrowerAccountRequest
import com.template.webserver.model.rest.request.borrower.UpdateBorrowerAccountRequest
import com.template.webserver.model.rest.response.CommonResponse
import com.template.webserver.services.borrower.BorrowerService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/borrower")
class BorrowerController(private val service: BorrowerService) {

    @PostMapping(value = ["/create"], produces = ["application/json"])
    fun create(@RequestBody request: CreateBorrowerAccountRequest): CommonResponse<BorrowerState.StateData> {
        return service.createAccount(request)
    }

    @PostMapping(value = ["/update"], produces = ["application/json"])
    fun update(@RequestBody request: UpdateBorrowerAccountRequest): CommonResponse<BorrowerState.StateData> {
        return service.updateAccount(request)
    }

    @PostMapping(value = ["/inquiryPaginated"], produces = ["application/json"])
    fun inquiryPaginated(@RequestBody request: RepositoryQueryParams): PaginatedResponse<BorrowerSchemaV1.BorrowerEntity> {
        return service.inquiryBorrowerPaginated(request)
    }

}