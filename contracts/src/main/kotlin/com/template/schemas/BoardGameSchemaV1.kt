package com.template.schemas

import net.corda.core.schemas.MappedSchema
import net.corda.core.schemas.PersistentState
import java.time.Instant
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Index
import javax.persistence.Table

object BoardGameSchema

object BoardGameSchemaV1 : MappedSchema(
    version = 1,
    schemaFamily = BoardGameSchema::class.java,
    mappedTypes = listOf(BoardGameEntity::class.java)
) {

    @Entity
    @Table(
        name = "boardgame",
        indexes = [Index(name = "idx_linear_id", columnList = "linear_id"), Index(
            name = "idx_board_game_code",
            columnList = "board_game_code"
        )]
    )
    class BoardGameEntity(

        @Column(name = "linear_id")
        val linearId: String,

        @Column(name = "board_game_code")
        val boardGameCode: String,

        @Column(name = "name")
        val name: String,

        @Column(name = "description", columnDefinition = "TEXT")
        val description: String,

        @Column(name = "genre")
        val genre: String,

        @Column(name = "min_player")
        val minPlayer: Int,

        @Column(name = "max_player")
        val maxPlayer: Int,

        @Column(name = "status")
        val status: String,

        @Column(name = "product_type")
        val productType: String,

        @Column(name = "additional_properties")
        val additionalProperties: String,

        @Column(name = "last_returned_date")
        val lastReturnedDate: Instant?,

        @Column(name = "purchased_date")
        val purchasedDate: Instant,

        @Column(name = "created_date")
        val createdDate: Instant,

        @Column(name = "modified_date")
        val modifiedDate: Instant?,

        @Column(name = "version")
        val version: Int

    ) : PersistentState()

}