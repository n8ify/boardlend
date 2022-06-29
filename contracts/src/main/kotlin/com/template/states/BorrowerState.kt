package com.template.states

import com.template.contracts.BorrowerContract
import com.template.info.UpdateBorrowerAccountInfo
import com.template.schemas.BorrowerSchemaV1
import com.template.schemas.BorrowerSchemaV2
import net.corda.core.contracts.BelongsToContract
import net.corda.core.contracts.LinearState
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.identity.AbstractParty
import net.corda.core.schemas.MappedSchema
import net.corda.core.schemas.PersistentState
import net.corda.core.schemas.QueryableState
import net.corda.core.serialization.CordaSerializable
import java.time.Instant

@BelongsToContract(BorrowerContract::class)
data class BorrowerState(
    val stateData: StateData,
    override val participants: List<AbstractParty>,
    override val linearId: UniqueIdentifier = UniqueIdentifier()
) : LinearState, QueryableState {

    @CordaSerializable
    data class StateData(
        val borrowerCode: String,
        val email: String,
        val tel: String,
        val name: String,
        val tier: String,
        val totalBorrow: Long,
        val isBorrowing: Boolean,
        val active: Boolean,
        val lastBorrowDate: Instant?,
        val createdDate: Instant,
        val modifiedDate: Instant?
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
                    modifiedDate = stateData.modifiedDate
                )
            }
            is BorrowerSchemaV2 -> {
                BorrowerSchemaV2.BorrowerEntity(
                    linearId = linearId.toString(),
                    borrowerCode = stateData.borrowerCode,
                    email = stateData.email,
                    name = stateData.name,
                    tel = stateData.tel,
                    tier = stateData.tier,
                    totalBorrow = stateData.totalBorrow,
                    isBorrowing = stateData.isBorrowing,
                    active = stateData.active,
                    lastBorrowDate = stateData.lastBorrowDate,
                    createdDate = stateData.createdDate,
                    modifiedDate = stateData.modifiedDate
                )
            }
            else -> throw IllegalArgumentException("Unrecognised schema $schema")
        }
    }

    override fun supportedSchemas(): Iterable<MappedSchema> {
        return listOf(BorrowerSchemaV1, BorrowerSchemaV2)
    }

    fun modify(info: UpdateBorrowerAccountInfo): BorrowerState {
        return copy(
            stateData = stateData.copy(
                email = info.email ?: stateData.email,
                name = info.name ?: stateData.name,
                tier = info.tier ?: stateData.tier,
                active = info.active ?: stateData.active,
                modifiedDate = Instant.now()
            )
        )
    }

    fun borrow(totalBorrowedBoardGame: Int): BorrowerState {
        return copy(
            stateData = stateData.copy(
                totalBorrow = this.stateData.totalBorrow + totalBorrowedBoardGame,
                isBorrowing = true,
                lastBorrowDate = Instant.now()
            )
        )
    }

    fun `return`(): BorrowerState {
        return copy(
            stateData = stateData.copy(
                isBorrowing = false
            )
        )
    }

}