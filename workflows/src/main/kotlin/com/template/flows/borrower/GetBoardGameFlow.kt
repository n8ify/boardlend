package com.template.flows.borrower

import co.paralleluniverse.fibers.Suspendable
import com.template.flows.AbstractFlowLogic
import com.template.repositories.BoardGameRepository
import com.template.states.BoardGameState
import com.template.states.LenderState
import net.corda.core.flows.InitiatingFlow
import net.corda.core.flows.StartableByRPC
import net.corda.core.node.services.Vault

@InitiatingFlow
@StartableByRPC
class GetBoardGameFlow(private val boardGameCode: String) : AbstractFlowLogic<Pair<BoardGameState, LenderState?>>() {

    override val flowName: String = "GetBoardGameFlow"

    @Suspendable
    override fun call(): Pair<BoardGameState, LenderState?> {
        val repository = serviceHub.cordaService(BoardGameRepository::class.java)

        val boardGameStateAndRef = repository.getBoardGameByBoardGameCode(boardGameCode, Vault.StateStatus.UNCONSUMED)
        val boardGameState = boardGameStateAndRef.state.data

        if (boardGameState.lenderReference.isResolved) {
            val lender = boardGameState.lenderReference.resolve(serviceHub)
            return boardGameState to lender.state.data
        }

        return boardGameState to null
    }
}