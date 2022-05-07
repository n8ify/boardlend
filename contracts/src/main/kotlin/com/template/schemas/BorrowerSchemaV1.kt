package com.template.schemas

import net.corda.core.schemas.MappedSchema
import net.corda.core.schemas.PersistentState
import org.hibernate.annotations.ColumnDefault
import java.time.Instant
import javax.persistence.*

object BorrowerSchema

object BorrowerSchemaV1 : MappedSchema(
    schemaFamily = BorrowerSchema::class.java,
    version = 1,
    mappedTypes = listOf(BorrowerEntity::class.java)
) {

    @Entity
    @Table(
        name = "borrower",
        indexes = [Index(name = "idx_linear_id", columnList = "linear_id"), Index(
            name = "idx_linear_id",
            columnList = "borrower_code"
        )]
    )
    class BorrowerEntity(

        @Column(name = "linear_id")
        val linearId: String,

        @Column(name = "borrower_code")
        val borrowerCode: String,

        @Column(name = "email")
        val email: String,

        @Column(name = "name")
        val name: String,

        @Column(name = "tier")
        val tier: String,

        @Column(name = "total_borrow")
        val totalBorrow: Long,

        @Column(name = "is_borrowing")
        val isBorrowing: Boolean,

        @Column(name = "active")
        val active: Boolean,

        @Column(name = "last_borrow_date")
        val lastBorrowDate: Instant?,

        @Column(name = "created_date")
        val createdDate: Instant,

        @Column(name = "modified_date")
        val modifiedDate: Instant?,

        @Version
        val version: Int

    ) : PersistentState()

}