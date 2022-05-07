package com.template.repositories

import com.template.schemas.BoardGameSchemaV1
import com.template.states.BoardGameState
import net.corda.core.contracts.StateAndRef
import net.corda.core.node.ServiceHub
import net.corda.core.node.services.CordaService
import net.corda.core.node.services.Vault
import net.corda.core.node.services.vault.QueryCriteria
import net.corda.core.node.services.vault.builder

@CordaService
class BoardGameRepository(private val serviceHub: ServiceHub) : AbstractBaseRepository() {

    fun getBoardGameByBoardGameCode(boardGameCode: String, stateStatus: Vault.StateStatus = Vault.StateStatus.UNCONSUMED) : StateAndRef<BoardGameState> {

        val expression = builder {
            BoardGameSchemaV1.BoardGameEntity::boardGameCode.equal(boardGameCode)
        }

        return serviceHub.vaultService.queryBy(
            contractStateType = BoardGameState::class.java,
            criteria = QueryCriteria.VaultCustomQueryCriteria(expression).withStatus(stateStatus)).states
            .singleOrNull() ?: throw IllegalArgumentException("Board game with code \"$boardGameCode\" is not found")

    }

}