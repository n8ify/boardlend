package com.template.schemas

import net.corda.core.schemas.MappedSchema
import net.corda.core.schemas.PersistentState
import org.hibernate.annotations.ColumnDefault
import java.time.Instant
import javax.persistence.*

object LenderSchema

object LenderSchemaV1 : MappedSchema(
    schemaFamily = BorrowerSchema::class.java,
    version = 1,
    mappedTypes = listOf(LenderEntity::class.java)
) {

    @Entity
    @Table(
        name = "lender",
        indexes = [Index(name = "idx_linear_id", columnList = "linear_id"), Index(
            name = "idx_lender_code",
            columnList = "lender_code"
        )]
    )
    class LenderEntity(

        @Column(name = "linear_id")
        val linearId: String,

        @Column(name = "lender_code")
        val lenderCode: String,

        @Column(name = "email")
        val email: String,

        @Column(name = "name")
        val name: String,

        @Column(name = "active")
        val active: Boolean,

        @Column(name = "created_date")
        val createdDate: Instant,

        @Column(name = "modified_date")
        val modifiedDate: Instant?,

        @Version
        val version: Int

    ) : PersistentState()

}