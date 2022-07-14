package com.template.repositories

import com.template.schemas.LenderSchemaV1
import com.template.states.LenderState
import net.corda.core.contracts.StateAndRef
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.node.ServiceHub
import net.corda.core.node.services.CordaService
import net.corda.core.node.services.Vault
import net.corda.core.node.services.vault.Builder.equal
import net.corda.core.node.services.vault.QueryCriteria
import net.corda.core.serialization.SingletonSerializeAsToken

@CordaService
class LenderRepository(private val serviceHub: ServiceHub) : SingletonSerializeAsToken() {

    fun getLenderById(id: String) : StateAndRef<LenderState> {
        val criteria = QueryCriteria.LinearStateQueryCriteria(
            linearId = listOf(UniqueIdentifier.fromString(id)),
            status = Vault.StateStatus.UNCONSUMED,
            contractStateTypes = setOf(LenderState::class.java)
        )
        return serviceHub.vaultService.queryBy(contractStateType = LenderState::class.java, criteria = criteria).states.single()
    }

    fun getLenderByCode(lenderCode: String) : StateAndRef<LenderState> {
        val expr = LenderSchemaV1.LenderEntity::lenderCode.equal(lenderCode)
        val criteria = QueryCriteria.VaultCustomQueryCriteria(
            status = Vault.StateStatus.UNCONSUMED,
            expression = expr
        )
        return serviceHub.vaultService.queryBy(contractStateType = LenderState::class.java, criteria = criteria).states.single()
    }

}