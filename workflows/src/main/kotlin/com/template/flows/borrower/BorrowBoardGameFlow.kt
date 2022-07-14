package com.template.flows.borrower

import co.paralleluniverse.fibers.Suspendable
import com.r3.corda.lib.accounts.workflows.internal.accountService
import com.r3.corda.lib.accounts.workflows.internal.flows.createKeyForAccount
import com.r3.corda.lib.accounts.workflows.services.AccountService
import com.template.contracts.BoardGameContract
import com.template.contracts.BorrowerContract
import com.template.flows.AbstractFlowLogic
import com.template.info.BorrowBoardGameInfo
import com.template.repositories.BoardGameRepository
import com.template.repositories.BorrowerRepository
import com.template.states.BoardGameState
import com.template.webserver.constants.enums.BoardGameStatus
import net.corda.core.contracts.Command
import net.corda.core.flows.*
import net.corda.core.identity.AnonymousParty
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder
import net.corda.core.utilities.ProgressTracker
import java.lang.IllegalArgumentException
import java.time.Instant

@InitiatingFlow
@StartableByRPC
class BorrowBoardGameFlow(private val info: BorrowBoardGameInfo): AbstractFlowLogic<SignedTransaction>() {


    companion object {
        object Progress {
            object GET_BORROWER_ACCOUNT_AND_STATE: ProgressTracker.Step("Get borrower account & state.")
            object MODIFY_BORROW_BORROWING_STATE: ProgressTracker.Step("Modify borrower's borrowing state.")
            object GET_TO_BORROW_BOARDGAME_STATE: ProgressTracker.Step("Get to borrow board game state.")
            object MODIFY_BORROW_BOARDGAME_STATE: ProgressTracker.Step("Modify borrow board game state.")
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
            Progress.GET_BORROWER_ACCOUNT_AND_STATE,
            Progress.MODIFY_BORROW_BORROWING_STATE,
            Progress.GET_TO_BORROW_BOARDGAME_STATE,
            Progress.MODIFY_BORROW_BOARDGAME_STATE,
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

        val boardGameRepository = serviceHub.cordaService(BoardGameRepository::class.java)
        val borrowerRepository = serviceHub.cordaService(BorrowerRepository::class.java)

        setCurrentProgressTracker(Progress.GET_BORROWER_ACCOUNT_AND_STATE)
        val borrowerAccount = serviceHub.accountService.accountInfo(info.borrowerCode).singleOrNull()
            ?: throw IllegalArgumentException("Account with \"${info.borrowerCode}\" is not found")
        val borrowerAccountParty = serviceHub.createKeyForAccount(borrowerAccount.state.data)
        val borrowerInputState = borrowerRepository.getBorrowerStateByBorrowerCode(info.borrowerCode)

        setCurrentProgressTracker(Progress.MODIFY_BORROW_BORROWING_STATE)
        val borrowerOutputState = borrowerInputState.state.data.borrow(info.boardGameCodes.size)

        setCurrentProgressTracker(Progress.GET_TO_BORROW_BOARDGAME_STATE)
        val boardGameInputStates = info.boardGameCodes.map { boardGameRepository.getBoardGameByBoardGameCode(it) }

        setCurrentProgressTracker(Progress.MODIFY_BORROW_BOARDGAME_STATE)
        val boardGameOutputStates = boardGameInputStates.map {
            it.state.data.copy(
                stateData = it.state.data.stateData.copy(
                    status = BoardGameStatus.Borrowing.name,
                    mustReturnedDate = info.mustReturnedDate,
                    modifiedDate = Instant.now()
                ),
                participants = info.participants + borrowerAccountParty
            )
        }

        setCurrentProgressTracker(Progress.TX_BUILDING_TRANSACTION)
        val command = Command(BoardGameContract.Commands.BorrowBoardGameCommand(), info.participants.map { it.owningKey })
        val txBuilder = TransactionBuilder(notary)
            .addCommand(command)

        txBuilder.addInputState(borrowerInputState)
        txBuilder.addOutputState(borrowerOutputState, BorrowerContract.ID)
        boardGameInputStates.forEach { txBuilder.addInputState(it) }
        boardGameOutputStates.forEach { txBuilder.addOutputState(it, BoardGameContract.ID) }

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

@InitiatedBy(BorrowBoardGameFlow::class)
class BorrowBoardGameFlowResponder(private val otherPartySession: FlowSession): FlowLogic<SignedTransaction>() {

    @Suspendable
    override fun call(): SignedTransaction {

        val signTransactionFlow = object: SignTransactionFlow(otherPartySession) {
            override fun checkTransaction(stx: SignedTransaction) {}
        }

        val txId = subFlow(signTransactionFlow).id
        return subFlow(ReceiveFinalityFlow(otherPartySession, expectedTxId = txId))
    }
}