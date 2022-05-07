package com.template.webserver.services.borrower

import com.template.domain.RepositoryQueryParams
import com.template.domain.PaginatedResponse
import com.template.schemas.BorrowerSchemaV1
import com.template.states.BorrowerState
import com.template.webserver.model.rest.request.borrower.CreateBorrowerAccountRequest
import com.template.webserver.model.rest.request.borrower.UpdateBorrowerAccountRequest
import com.template.webserver.model.rest.response.CommonResponse

interface BorrowerService {

    fun createAccount(request: CreateBorrowerAccountRequest): CommonResponse<BorrowerState.StateData>
    fun updateAccount(request: UpdateBorrowerAccountRequest): CommonResponse<BorrowerState.StateData>

    fun inquiryBorrowerPaginated(request: RepositoryQueryParams): PaginatedResponse<BorrowerSchemaV1.BorrowerEntity>

}