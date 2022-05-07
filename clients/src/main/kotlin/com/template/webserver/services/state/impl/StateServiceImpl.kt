package com.template.webserver.services.state.impl

import com.template.domain.TrxReference
import com.template.webserver.constants.enums.ResponseCode
import com.template.webserver.constants.enums.ResponseStatus
import com.template.webserver.model.rest.response.CommonResponse
import com.template.webserver.services.AbstractBaseService
import com.template.webserver.services.state.StateService
import org.springframework.stereotype.Service

@Service
class StateServiceImpl : AbstractBaseService(), StateService {


    override fun inquiryStateStatusByTrxReference(trxReference: TrxReference): CommonResponse<TrxReference> {
        val stateStatus = inquiryStateStatus(trxReference)
        return CommonResponse<TrxReference>(
            status = ResponseStatus.Success,
            message = "Transaction \"$trxReference\" is ${stateStatus.name}",
            code = ResponseCode.S00000,
            data = trxReference
        )
    }

}