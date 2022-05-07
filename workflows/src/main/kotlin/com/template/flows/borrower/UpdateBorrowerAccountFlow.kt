package com.template.flows.borrower

import co.paralleluniverse.fibers.Suspendable
import com.template.contracts.BorrowerContract
import com.template.flows.AbstractFlowLogic
import com.template.info.UpdateBorrowerAccountInfo
import com.template.repositories.BorrowerRepository
import net.corda.core.contracts.Command
import net.corda.core.flows.*
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder
import net.corda.core.utilities.ProgressTracker
import java.time.Instant

@InitiatingFlow
@StartableByRPC
class UpdateBorrowerAccountFlow(private val info: UpdateBorrowerAccountInfo) : AbstractFlowLogic<SignedTransaction>() {

    companion object {
        object Progress {
            object GET_EXISTED_BORROWER_STATE: ProgressTracker.Step("Get existed borrower's state.")
            object MODIFY_BORROWER_STATE: ProgressTracker.Step("Modify borrower's state.")
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
            Progress.GET_EXISTED_BORROWER_STATE,
            Progress.MODIFY_BORROWER_STATE,
            Progress.TX_BUILDING_TRANSACTION,
            Progress.TX_VERIFICATION,
            Progress.SIGS_GATHERING,
            Progress.FINALISATION,
            Progress.FLOW_COMPLETED
        )

    }

    override val progressTracker: ProgressTracker = tracker()

    override val flowName: String = "CreateBorrowerAccountFlow"

    @Suspendable
    override fun call() : SignedTransaction {

        /* Initial Components */
        val borrowerService = serviceHub.cordaService(BorrowerRepository::class.java)

        setCurrentProgressTracker(Progress.GET_EXISTED_BORROWER_STATE)
        val inputState = borrowerService.getBorrowerStateByLinearId(info.linearId)

        setCurrentProgressTracker(Progress.MODIFY_BORROWER_STATE)
        val outputState = inputState.state.data.modify(info = info)

        setCurrentProgressTracker(Progress.TX_BUILDING_TRANSACTION)
        val commands = Command(
            value = BorrowerContract.Commands.UpdateBorrowerAccountCommand(),
            signers = inputState.state.data.participants.map { it.owningKey }
        )
        val txBuilder = TransactionBuilder(notary)
            .addCommand(commands)
            .addInputState(inputState)
            .addOutputState(outputState, BorrowerContract.ID)

        setCurrentProgressTracker(Progress.TX_VERIFICATION)
        txBuilder.verify(serviceHub)

        setCurrentProgressTracker(Progress.SIGS_GATHERING)
        val ptx = serviceHub.signInitialTransaction(txBuilder)
        val sessions = info.participants.filter { it != ourIdentity }.map { initiateFlow(it) }
        val stx = subFlow(CollectSignaturesFlow( ptx, sessions))

        setCurrentProgressTracker(Progress.FINALISATION)
        return subFlow(FinalityFlow(stx, sessions))
    }

}

@InitiatedBy(UpdateBorrowerAccountFlow::class)
class UpdateBorrowerAccountFlowResponder(private val counterPartySessions: FlowSession) : FlowLogic<SignedTransaction>() {

    @Suspendable
    override fun call(): SignedTransaction {

        val signTrxFlow = object : SignTransactionFlow(counterPartySessions) {
            override fun checkTransaction(stx: SignedTransaction) {}
        }

        val txId = subFlow(signTrxFlow).id
        return subFlow(ReceiveFinalityFlow(counterPartySessions, expectedTxId = txId))
    }

}