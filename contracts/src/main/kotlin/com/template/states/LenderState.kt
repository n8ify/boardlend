package com.template.states

import com.template.info.UpdateBorrowerAccountInfo
import com.template.schemas.BorrowerSchemaV1
import net.corda.core.contracts.BelongsToContract
import net.corda.core.contracts.LinearState
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.identity.AbstractParty
import net.corda.core.schemas.MappedSchema
import net.corda.core.schemas.PersistentState
import net.corda.core.schemas.QueryableState
import net.corda.core.serialization.CordaSerializable
import java.time.Instant

@BelongsToContract()
data class LenderState(
    val stateData: StateData,
    override val participants: List<AbstractParty>,
    override val linearId: UniqueIdentifier = UniqueIdentifier()
) : LinearState, QueryableState {

    @CordaSerializable
    data class StateData(
        val borrowerCode: String,
        val email: String,
        val name: String,
        val active: Boolean,
        val createdDate: Instant,
        val modifiedDate: Instant?,
        val version: Int = 0
    )

    override fun generateMappedObject(schema: MappedSchema): PersistentState {
        return when (schema) {
            is BorrowerSchemaV1 -> {
                BorrowerSchemaV1.BorrowerEntity(
                    linearId = linearId.toString(),
                    borrowerCode = stateData.borrowerCode,
                    email = stateData.email,
                    name = stateData.name,
                    tier = stateData.tier,
                    totalBorrow = stateData.totalBorrow,
                    isBorrowing = stateData.isBorrowing,
                    active = stateData.active,
                    lastBorrowDate = stateData.lastBorrowDate,
                    createdDate = stateData.createdDate,
                    modifiedDate = stateData.modifiedDate,
                    version = stateData.version
                )
            }
            else -> throw IllegalArgumentException("Unrecognised schema $schema")
        }
    }

    override fun supportedSchemas(): Iterable<MappedSchema> {
        return listOf(BorrowerSchemaV1)
    }

}
