package com.template.flows

import co.paralleluniverse.fibers.Suspendable
import com.template.states.BoardGameState
import net.corda.core.contracts.StateRef
import net.corda.core.flows.FlowLogic
import net.corda.core.flows.InitiatingFlow
import net.corda.core.flows.SchedulableFlow

@InitiatingFlow
@SchedulableFlow
class RemindMustReturnBoardgameFlow(private val stateRef: StateRef) : FlowLogic<Unit>() {

    @Suspendable
    override fun call() {
        val stateAndRef = serviceHub.toStateAndRef<BoardGameState>(stateRef)
        val stateData = stateAndRef.state.data.stateData
        val note = "\"${stateData.name} (${stateData.boardGameCode})\" is nearly to returned date (${stateData.mustReturnedDate})"
        serviceHub.vaultService.addNoteToTransaction(stateAndRef.ref.txhash, note)
        println(note)
    }

}