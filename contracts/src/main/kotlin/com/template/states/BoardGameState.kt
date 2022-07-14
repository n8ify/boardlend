package com.template.states

import com.template.contracts.BoardGameContract
import com.template.schemas.BoardGameSchemaV1
import com.template.schemas.BoardGameSchemaV2
import com.template.schemas.BorrowerSchemaV1
import net.corda.core.contracts.*
import net.corda.core.flows.FlowLogicRefFactory
import net.corda.core.identity.AbstractParty
import net.corda.core.schemas.MappedSchema
import net.corda.core.schemas.PersistentState
import net.corda.core.schemas.QueryableState
import net.corda.core.serialization.ConstructorForDeserialization
import net.corda.core.serialization.CordaSerializable
import java.time.Instant

@BelongsToContract(BoardGameContract::class)
data class BoardGameState(
    val stateData: StateData,
    val lenderReference: LinearPointer<LenderState>,
    override val linearId: UniqueIdentifier = UniqueIdentifier(),
    override val participants: List<AbstractParty>
) : LinearState, QueryableState, SchedulableState {

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
        val mustReturnedDate: Instant,
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
            is BoardGameSchemaV2 -> {
                BoardGameSchemaV2.BoardGameEntity(
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
                    mustReturnedDate = stateData.mustReturnedDate,
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
        return listOf(BoardGameSchemaV1, BoardGameSchemaV2)
    }

    override fun nextScheduledActivity(
        thisStateRef: StateRef,
        flowLogicRefFactory: FlowLogicRefFactory
    ): ScheduledActivity? {
        return ScheduledActivity(flowLogicRefFactory.create("com.template.flows.RemindMustReturnBoardgameFlow", thisStateRef), stateData.mustReturnedDate.minusSeconds(60))
    }
}