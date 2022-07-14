package com.template.webserver.services.lender.impl

import com.template.flows.lender.CreateLenderAccountFlow
import com.template.flows.lender.UpdateLenderAccountFlow
import com.template.info.CreateLenderAccountInfo
import com.template.info.UpdateLenderAccountInfo
import com.template.states.LenderState
import com.template.webserver.constants.enums.ResponseCode
import com.template.webserver.constants.enums.ResponseStatus
import com.template.webserver.model.rest.request.lender.CreateLenderAccountRequest
import com.template.webserver.model.rest.request.lender.UpdateLenderAccountRequest
import com.template.webserver.model.rest.response.CommonResponse
import com.template.webserver.services.AbstractBaseService
import com.template.webserver.services.lender.LenderService
import net.corda.core.messaging.startTrackedFlow
import net.corda.core.utilities.getOrThrow
import org.springframework.stereotype.Service

@Service
class LenderServiceImpl : AbstractBaseService(), LenderService {

    override fun create(request: CreateLenderAccountRequest): CommonResponse<LenderState.StateData> {

        val info = CreateLenderAccountInfo(
            lenderCode = request.lenderCode,
            email = request.email,
            name = request.name,
            active = request.active,
            participants = request.participants.map { obtainPartyByName(it) }
        )

        val result = rpc.proxy.startTrackedFlow(::CreateLenderAccountFlow, info)
            .returnValue.getOrThrow().tx.outputsOfType(LenderState::class.java).single()

        return CommonResponse(
            status = ResponseStatus.Success,
            code = ResponseCode.S00000,
            message = "Create lender success with linear id \"${result.linearId}\"",
            data = result.stateData
        )

    }

    override fun update(request: UpdateLenderAccountRequest): CommonResponse<LenderState.StateData> {
        val info = UpdateLenderAccountInfo(
            lenderCode = request.lenderCode,
            email = request.email,
            name = request.name,
            active = request.active
        )

        val result = rpc.proxy.startTrackedFlow(::UpdateLenderAccountFlow, info)
            .returnValue.getOrThrow().tx.outputsOfType(LenderState::class.java).single()

        return CommonResponse(
            status = ResponseStatus.Success,
            code = ResponseCode.S00000,
            message = "Update lender success with linear id \"${result.linearId}\"",
            data = result.stateData
        )

    }
}