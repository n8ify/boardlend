package com.template.flows.borrower

import co.paralleluniverse.fibers.Suspendable
import com.template.domain.PaginatedResponse
import com.template.repositories.BorrowerRepository
import com.template.repositories.RepositoryQueryParams
import com.template.schemas.BorrowerSchemaV1
import net.corda.core.flows.FlowLogic
import net.corda.core.flows.InitiatingFlow
import net.corda.core.flows.StartableByRPC

@InitiatingFlow
@StartableByRPC
class GetBorrowerPaginatedFlow(private val repositoryQueryParams: RepositoryQueryParams): FlowLogic<PaginatedResponse<BorrowerSchemaV1.BorrowerEntity>>() {

    @Suspendable
    override fun call(): PaginatedResponse<BorrowerSchemaV1.BorrowerEntity> {

        val service = serviceHub.cordaService(BorrowerRepository::class.java)
        return service.getBorrowerPaginated(repositoryQueryParams)

    }
}