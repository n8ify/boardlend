package com.template.states

import com.template.contracts.BoardGameContract
import com.template.schemas.BoardGameSchemaV1
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

@BelongsToContract(BoardGameContract::class)
data class BoardGameState(
    val stateData: StateData,
    override val linearId: UniqueIdentifier = UniqueIdentifier(),
    override val participants: List<AbstractParty>
) : LinearState, QueryableState {

    @CordaSerializable
    data class StateData (
        val boardGameCode: String,
        val name: String,
        val description: String,
        val genre: String,
        val minPlayer: Int,
        val maxPlayer: Int,
        val status: String,
        val productType: String,
        val additionalProperties: String,
        val lastReturnedDate: Instant?,
        val purchasedDate: Instant,
        val createdDate: Instant,
        val modifiedDate: Instant?,
        val version: Int = 0
    )

    override fun generateMappedObject(schema: MappedSchema): PersistentState {
        return when (schema) {
            is BoardGameSchemaV1 -> {
                BoardGameSchemaV1.BoardGameEntity(
                    linearId = linearId.toString(),
                    boardGameCode = stateData.boardGameCode,
                    name = stateData.name,
                    description = stateData.description,
                    genre = stateData.genre,
                    minPlayer = stateData.minPlayer,
                    maxPlayer = stateData.maxPlayer,
                    status = stateData.status,
                    productType = stateData.productType,
                    additionalProperties = stateData.additionalProperties,
                    lastReturnedDate = stateData.lastReturnedDate,
                    purchasedDate = stateData.purchasedDate,
                    createdDate =  stateData.createdDate,
                    modifiedDate = stateData.modifiedDate,
                    version = stateData.version
                )
            }
            else -> throw IllegalArgumentException("Unrecognised schema $schema")
        }
    }

    override fun supportedSchemas(): Iterable<MappedSchema> {
        return listOf(BoardGameSchemaV1)
    }
}