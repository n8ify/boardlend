package com.template.flows

import co.paralleluniverse.fibers.Suspendable
import com.template.domain.TrxReference
import com.template.service.StateService
import net.corda.core.contracts.StateRef
import net.corda.core.flows.FlowLogic
import net.corda.core.flows.InitiatingFlow
import net.corda.core.flows.StartableByRPC
import net.corda.core.node.services.Vault

@InitiatingFlow
@StartableByRPC
class GetStateStatusFlow(private val trxReference: TrxReference) : FlowLogic<Vault.StateStatus>() {

    @Suspendable
    override fun call() : Vault.StateStatus {
        /* Initial Components */
        val stateService = serviceHub.cordaService(StateService::class.java)
        return stateService.getStateStatusByTrxReference(trxReference)
    }

}