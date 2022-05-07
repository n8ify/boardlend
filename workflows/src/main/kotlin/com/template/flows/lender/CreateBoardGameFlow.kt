package com.template.flows.lender

import co.paralleluniverse.fibers.Suspendable
import com.template.contracts.BoardGameContract
import com.template.flows.AbstractFlowLogic
import com.template.info.CreateBoardGameInfo
import com.template.states.BoardGameState
import net.corda.core.contracts.Command
import net.corda.core.flows.*
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder
import net.corda.core.utilities.ProgressTracker
import java.time.Instant

@InitiatingFlow
@StartableByRPC
class CreateBoardGameFlow(private val info: CreateBoardGameInfo): AbstractFlowLogic<SignedTransaction>() {


    companion object {
        object Progress {
            object CREATE_BOARDGAME_STATE: ProgressTracker.Step("Create a borrower's state.")
            object TX_BUILDING_TRANSACTION  : ProgressTracker.Step("Building transaction.")
            object TX_VERIFICATION  : ProgressTracker.Step("Verifying transaction.")
            object SIGS_GATHERING  : ProgressTracker.Step("Gathering a transaction's signatures.") {
                override fun childProgressTracker(): ProgressTracker = CollectSignaturesFlow.tracker()
            }
            object FINALISATION   : ProgressTracker.Step("Finalisation") {
                override fun childProgressTracker(): ProgressTracker = FinalityFlow.tracker()
            }
            object FLOW_COMPLETED: ProgressTracker.Step("Flow Completed")
        }

        fun tracker() = ProgressTracker(
            Progress.CREATE_BOARDGAME_STATE,
            Progress.TX_BUILDING_TRANSACTION,
            Progress.TX_VERIFICATION,
            Progress.SIGS_GATHERING,
            Progress.FINALISATION,
            Progress.FLOW_COMPLETED
        )

    }

    override val progressTracker: ProgressTracker = tracker()

    override val flowName: String = "CreateBoardGameFlow"

    @Suspendable
    override fun call(): SignedTransaction {

        setCurrentProgressTracker(Progress.CREATE_BOARDGAME_STATE)
        val stateData = BoardGameState.StateData(
            boardGameCode = info.boardGameCode,
            name = info.name,
            description = info.description,
            genre = info.genre,
            minPlayer = info.minPlayer,
            maxPlayer = info.maxPlayer,
            status = info.status,
            productType = info.productType,
            additionalProperties = info.additionalProperties,
            lastReturnedDate = null,
            purchasedDate = info.purchasedDate,
            createdDate = Instant.now(),
            modifiedDate = null
        )
        val output = BoardGameState(
            stateData = stateData,
            participants = info.participants
        )

        setCurrentProgressTracker(Progress.TX_BUILDING_TRANSACTION)
        val command = Command(BoardGameContract.Commands.CreateBoardGameCommand(), info.participants.map { it.owningKey })
        val txBuilder = TransactionBuilder(notary)
            .addCommand(command)
            .addOutputState(output, BoardGameContract.ID)

        setCurrentProgressTracker(Progress.TX_VERIFICATION)
        txBuilder.verify(serviceHub)

        setCurrentProgressTracker(Progress.SIGS_GATHERING)
        val ptx = serviceHub.signInitialTransaction(txBuilder)
        val sessions = info.participants.filter { it != ourIdentity }.map { initiateFlow(it) }
        val stx = subFlow(CollectSignaturesFlow(ptx, sessions))

        setCurrentProgressTracker(Progress.FINALISATION)
        return subFlow(FinalityFlow(stx, sessions))


    }

}

@InitiatedBy(CreateBoardGameFlow::class)
class CreateBoardGameFlowResponder(private val otherPartySession: FlowSession): FlowLogic<SignedTransaction>() {

    @Suspendable
    override fun call(): SignedTransaction {

        val signTransactionFlow = object: SignTransactionFlow(otherPartySession) {
            override fun checkTransaction(stx: SignedTransaction) {}
        }

        val txId = subFlow(signTransactionFlow).id
        return subFlow(ReceiveFinalityFlow(otherPartySession, expectedTxId = txId))
    }
}