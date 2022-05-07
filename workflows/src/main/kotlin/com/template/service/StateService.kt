package com.template.service

import com.template.domain.TrxReference
import net.corda.core.contracts.LinearState
import net.corda.core.contracts.StateAndRef
import net.corda.core.contracts.StateRef
import net.corda.core.crypto.SecureHash
import net.corda.core.node.ServiceHub
import net.corda.core.node.services.CordaService
import net.corda.core.node.services.Vault
import net.corda.core.serialization.SingletonSerializeAsToken

@CordaService
class StateService(private val serviceHub: ServiceHub) : SingletonSerializeAsToken() {

    fun <T : LinearState> getStateAndReferenceByTrxReference(trxReference: TrxReference): StateAndRef<LinearState> {
        val stateRef = StateRef(SecureHash.parse(trxReference.transactionId), trxReference.outputIndex)
        return serviceHub.toStateAndRef<T>(stateRef)
    }

    fun getStateStatusByTrxReference(trxReference: TrxReference) : Vault.StateStatus = serviceHub.withEntityManager {
        val query = """
            SELECT vs.stateStatus FROM VaultSchemaV1${'$'}VaultStates AS vs
            WHERE vs.stateRef = :stateRef
        """.trimIndent()
        val result = createQuery(query).apply {
            setParameter("stateRef", trxReference.toPersistentState())
        }.resultList.singleOrNull() ?: throw  IllegalArgumentException("Transaction with \"$trxReference\" is not existed.")
        result as Vault.StateStatus
    }

}