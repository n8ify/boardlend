package com.template.webserver.services.borrower.impl

import com.template.domain.PaginatedResponse
import com.template.flows.borrower.CreateBorrowerAccountFlow
import com.template.flows.borrower.GetBorrowerPaginatedFlow
import com.template.flows.borrower.UpdateBorrowerAccountFlow
import com.template.info.CreateBorrowerAccountInfo
import com.template.info.UpdateBorrowerAccountInfo
import com.template.schemas.BorrowerSchemaV1
import com.template.states.BorrowerState
import com.template.webserver.constants.enums.ResponseCode
import com.template.webserver.constants.enums.ResponseStatus
import com.template.webserver.model.rest.request.BasicRepositoryQueryRequest
import com.template.webserver.model.rest.request.borrower.CreateBorrowerAccountRequest
import com.template.webserver.model.rest.request.borrower.UpdateBorrowerAccountRequest
import com.template.webserver.model.rest.response.CommonResponse
import com.template.webserver.services.AbstractBaseService
import com.template.webserver.services.borrower.BorrowerService
import net.corda.core.messaging.startFlow
import net.corda.core.messaging.startTrackedFlow
import net.corda.core.utilities.getOrThrow
import org.springframework.stereotype.Service

@Service
class BorrowerServiceImpl : AbstractBaseService(), BorrowerService {

    override fun createAccount(request: CreateBorrowerAccountRequest): CommonResponse<BorrowerState.StateData> {
        val createBorrowerAccountInfo = CreateBorrowerAccountInfo(
            borrowerCode = request.borrowerCode,
            email = request.email,
            tel = request.tel,
            name = request.name,
            tier = request.tier,
            active = request.active,
            participantLenderCode = request.participantLenderCode
        )

        val result = rpc.proxy.startTrackedFlow(::CreateBorrowerAccountFlow, createBorrowerAccountInfo)
            .returnValue.getOrThrow().tx.outRefsOfType<BorrowerState>()
        val state = result.single().state

        return CommonResponse(
            status = ResponseStatus.Success,
            code = ResponseCode.S00000,
            message = "Borrower account \"${state.data.stateData.name}\" is created with id \"${state.data.linearId}\"",
            data = state.data.stateData
        )
    }

    override fun updateAccount(request: UpdateBorrowerAccountRequest): CommonResponse<BorrowerState.StateData> {
        val updateBorrowerAccountInfo = UpdateBorrowerAccountInfo(
            linearId = request.linearId,
            email = request.email,
            name = request.name,
            tier = request.tier,
            active = request.active,
            participants =  request.participants.map { obtainPartyByName(it) },
            version = request.version
        )

        val result = rpc.proxy.startTrackedFlow(::UpdateBorrowerAccountFlow, updateBorrowerAccountInfo)
            .returnValue.getOrThrow().tx.outRefsOfType<BorrowerState>()
        val state = result.single().state

        return CommonResponse(
            status = ResponseStatus.Success,
            code = ResponseCode.S00000,
            message = "Borrower account \"${state.data.stateData.name}\" is updated with id \"${state.data.linearId}\"",
            data = state.data.stateData
        )
    }

    override fun inquiryBorrowerPaginated(request: BasicRepositoryQueryRequest): PaginatedResponse<BorrowerSchemaV1.BorrowerEntity> {
        return rpc.proxy.startFlow(::GetBorrowerPaginatedFlow, request.toRepositoryQueryParams()).returnValue.getOrThrow()
    }
}