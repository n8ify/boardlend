package com.template.repositories

import com.template.constants.DateFormatConstant.yyyy_MM_dd_Dash
import com.template.domain.RepositoryQueryParams
import com.template.domain.PaginatedResponse
import com.template.schemas.BorrowerSchemaV1
import com.template.states.BorrowerState
import net.corda.core.contracts.StateAndRef
import net.corda.core.node.ServiceHub
import net.corda.core.node.services.CordaService
import net.corda.core.node.services.Vault
import net.corda.core.node.services.vault.QueryCriteria
import net.corda.core.node.services.vault.builder
import javax.persistence.Column

@CordaService
class BorrowerRepository(private val serviceHub: ServiceHub): AbstractBaseRepository() {

    companion object {

        val allowedBorrowerSortFields = BorrowerSchemaV1.BorrowerEntity::class.java.declaredFields
            .filter { it.isAnnotationPresent(Column::class.java) }
            .map { it.name }

    }

    fun getBorrowerStateByLinearId(linearId: String): StateAndRef<BorrowerState> {
        val expression = builder {
            BorrowerSchemaV1.BorrowerEntity::linearId.equal(linearId)
        }
        return serviceHub.vaultService.queryBy(
            contractStateType = BorrowerState::class.java,
            criteria = QueryCriteria.VaultCustomQueryCriteria(expression).withStatus(Vault.StateStatus.UNCONSUMED)
        ).states.singleOrNull() ?: throw IllegalStateException("Borrower with linear id \"$linearId\" is not found")
    }

    fun getBorrowerStateByBorrowerCode(borrowerCode: String): StateAndRef<BorrowerState> {
        val expression = builder {
            BorrowerSchemaV1.BorrowerEntity::borrowerCode.equal(borrowerCode)
        }
        return serviceHub.vaultService.queryBy(
            contractStateType = BorrowerState::class.java,
            criteria = QueryCriteria.VaultCustomQueryCriteria(expression).withStatus(Vault.StateStatus.UNCONSUMED)
        ).states.singleOrNull() ?: throw IllegalStateException("Borrower with borrower code \"$borrowerCode\" is not found")
    }

    fun getBorrowerPaginated(repositoryQueryParams: RepositoryQueryParams) : PaginatedResponse<BorrowerSchemaV1.BorrowerEntity> {

        val queryStr = """
            SELECT %s
            FROM BorrowerSchemaV1${'$'}BorrowerEntity br
                INNER JOIN VaultSchemaV1${'$'}VaultStates br_vs ON br_vs.stateRef = br.stateRef 
                    AND br_vs.stateStatus = '${Vault.StateStatus.UNCONSUMED.ordinal}'
            WHERE 1 = 1
        """.trimIndent()

        val queryParams = repositoryQueryParams.getSearchTermsAsMap()

        val paramToPredicateMap = mapOf(
            "linearId".mapToPredicate("br.linearId", Operation.Equal),
            "borrowerCode".mapToPredicate("br.borrowerCode", Operation.Equal),
            "email".mapToPredicate("br.email", Operation.Equal),
            "tier".mapToPredicate("br.tier", Operation.Equal),
            "totalBorrow".mapToPredicate("br.totalBorrow", Operation.Equal, Long::class),
            "isBorrowing".mapToPredicate("br.isBorrowing", Operation.Equal),
            "active".mapToPredicate("br.active", Operation.Equal),
            "lastBorrowDate".mapToPredicate("br.lastBorrowDate", Operation.Equal),
            "name".mapToPredicate("br.name", Operation.Like),
            "emailMul".mapToIgnoreCasePredicate("br.email", Operation.In, queryParams),
            "nameMul".mapToIgnoreCasePredicate("br.name", Operation.In, queryParams),
            "createdDateFrom".mapToDatePredicate("br.createdDate", yyyy_MM_dd_Dash, Operation.GreaterThanOrEqual),
            "createdDateTo".mapToDatePredicate("br.createdDate", yyyy_MM_dd_Dash, Operation.LessThanOrEqual),
            "modifiedDateFrom".mapToDatePredicate("br.modifiedDate", yyyy_MM_dd_Dash, Operation.GreaterThanOrEqual),
            "modifiedDateTo".mapToDatePredicate("br.modifiedDate", yyyy_MM_dd_Dash, Operation.LessThanOrEqual)
        )
        val sortColumnAndOrder = repositoryQueryParams.createSortOrderStatement(listOf(
            allowedBorrowerSortFields to "br"
        ))

        return serviceHub.withEntityManager {

            val countQuery = getParametrizedQuery(
                queryParams = queryParams,
                paramToPredicateMap = paramToPredicateMap,
                baseQueryString = queryStr.format(COUNT_ALL)
            )

            val resultCount = countQuery.resultList.first() as Long

            val query = getParametrizedQuery(
                queryParams = queryParams,
                paramToPredicateMap = paramToPredicateMap,
                baseQueryString = queryStr.format("br"),
                sortColumnAndOrder = sortColumnAndOrder
            )

            query.firstResult = (repositoryQueryParams.startPage - 1) * repositoryQueryParams.pageSize
            query.maxResults = repositoryQueryParams.pageSize

            val resultQuery = query.resultList.map { it as BorrowerSchemaV1.BorrowerEntity }

            PaginatedResponse(
                result = resultQuery,
                totalResults = resultCount,
                pageSize = repositoryQueryParams.pageSize,
                pageNumber = repositoryQueryParams.startPage
            )
        }

    }


}