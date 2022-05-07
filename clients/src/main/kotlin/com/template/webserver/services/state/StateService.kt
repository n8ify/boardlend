package com.template.webserver.services.state

import com.template.domain.TrxReference
import com.template.webserver.model.rest.response.CommonResponse

interface StateService {

    fun inquiryStateStatusByTrxReference(trxReference: TrxReference): CommonResponse<TrxReference>

}