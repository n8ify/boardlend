package com.template.flows.borrower

import co.paralleluniverse.fibers.Suspendable
import com.r3.corda.lib.accounts.workflows.flows.CreateAccount
import com.r3.corda.lib.accounts.workflows.flows.ShareAccountInfo
import com.template.contracts.BorrowerContract
import com.template.flows.AbstractFlowLogic
import com.template.info.CreateBorrowerAccountInfo
import com.template.states.BorrowerState
import net.corda.core.contracts.Command
import net.corda.core.flows.*
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder
import net.corda.core.utilities.ProgressTracker
import java.time.Instant

@InitiatingFlow
@StartableByRPC
class CreateBorrowerAccountFlow(private val info: CreateBorrowerAccountInfo) : AbstractFlowLogic<SignedTransaction>() {

    companion object {
        object Progress {
            object CREATE_BORROWER_CORDA_ACCOUNT: ProgressTracker.Step("Create corda borrower account")
            object SHARE_BORROWER_CORDA_ACCOUNT: ProgressTracker.Step("Share corda borrower account to parties")
            object CREATE_BORROWER_STATE: ProgressTracker.Step("Create a borrower's state.")
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
            Progress.CREATE_BORROWER_CORDA_ACCOUNT,
            Progress.SHARE_BORROWER_CORDA_ACCOUNT,
            Progress.CREATE_BORROWER_STATE,
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

        setCurrentProgressTracker(Progress.CREATE_BORROWER_CORDA_ACCOUNT)
        val createAccountTx = subFlow(CreateAccount(name = info.borrowerCode))

        setCurrentProgressTracker(Progress.SHARE_BORROWER_CORDA_ACCOUNT)
        subFlow(ShareAccountInfo(createAccountTx, info.participants))

        setCurrentProgressTracker(Progress.CREATE_BORROWER_STATE)
        val stateData = BorrowerState.StateData(
            borrowerCode = info.borrowerCode,
            email = info.email,
            name = info.name,
            tier = info.tier,
            totalBorrow = 0,
            isBorrowing = false,
            active = info.active,
            lastBorrowDate = null,
            createdDate = Instant.now(),
            modifiedDate = null
        )
        val output = BorrowerState(
            stateData = stateData,
            linearId = createAccountTx.state.data.linearId,
            participants = info.participants
        )

        setCurrentProgressTracker(Progress.TX_BUILDING_TRANSACTION)
        val commands = Command(
            value = BorrowerContract.Commands.CreateBorrowerAccountCommand(),
            signers = info.participants.map { it.owningKey }
        )
        val txBuilder = TransactionBuilder(notary)
            .addCommand(commands)
            .addOutputState(output, BorrowerContract.ID)

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

@InitiatedBy(CreateBorrowerAccountFlow::class)
class CreateBorrowerAccountFlowResponder(private val counterPartySessions: FlowSession) : FlowLogic<SignedTransaction>() {

    @Suspendable
    override fun call(): SignedTransaction {

        val signTrxFlow = object : SignTransactionFlow(counterPartySessions) {
            override fun checkTransaction(stx: SignedTransaction) {}
        }

        val txId = subFlow(signTrxFlow).id
        return subFlow(ReceiveFinalityFlow(counterPartySessions, expectedTxId = txId))
    }

}