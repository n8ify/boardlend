package com.template.states

import com.template.contracts.LenderContract
import com.template.info.UpdateBorrowerAccountInfo
import com.template.info.UpdateLenderAccountInfo
import com.template.schemas.BorrowerSchemaV1
import com.template.schemas.LenderSchemaV1
import net.corda.core.contracts.BelongsToContract
import net.corda.core.contracts.LinearState
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.identity.AbstractParty
import net.corda.core.schemas.MappedSchema
import net.corda.core.schemas.PersistentState
import net.corda.core.schemas.QueryableState
import net.corda.core.serialization.CordaSerializable
import java.time.Instant

@BelongsToContract(LenderContract::class)
data class LenderState(
    val stateData: StateData,
    override val participants: List<AbstractParty>,
    override val linearId: UniqueIdentifier = UniqueIdentifier()
) : LinearState, QueryableState {

    @CordaSerializable
    data class StateData(
        val lenderCode: String,
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
                LenderSchemaV1.LenderEntity(
                    linearId = linearId.toString(),
                    lenderCode = stateData.lenderCode,
                    email = stateData.email,
                    name = stateData.name,
                    active = stateData.active,
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

    fun modify(info: UpdateLenderAccountInfo): LenderState {
        return copy(
            stateData = stateData.copy(
                email = info.email ?: stateData.email,
                name = info.name ?: stateData.name,
                active = info.active ?: stateData.active
            )
        )
    }

}
