package com.template.flows.lender

import co.paralleluniverse.fibers.Suspendable
import com.r3.corda.lib.accounts.workflows.flows.CreateAccount
import com.r3.corda.lib.accounts.workflows.flows.ShareAccountInfo
import com.template.contracts.BorrowerContract
import com.template.contracts.LenderContract
import com.template.flows.AbstractFlowLogic
import com.template.info.CreateBorrowerAccountInfo
import com.template.info.CreateLenderAccountInfo
import com.template.states.BorrowerState
import com.template.states.LenderState
import net.corda.core.contracts.Command
import net.corda.core.flows.*
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder
import net.corda.core.utilities.ProgressTracker
import java.time.Instant

@InitiatingFlow
@StartableByRPC
@Deprecated("Unused")
class CreateLenderAccountFlow(private val info: CreateLenderAccountInfo) : AbstractFlowLogic<SignedTransaction>() {

    companion object {
        object Progress {
            object CREATE_LENDER_CORDA_ACCOUNT: ProgressTracker.Step("Create corda lender account")
            object SHARE_LENDER_CORDA_ACCOUNT: ProgressTracker.Step("Share corda lender account to parties")
            object CREATE_LENDER_STATE: ProgressTracker.Step("Create a lender's state.")
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
            Progress.CREATE_LENDER_CORDA_ACCOUNT,
            Progress.SHARE_LENDER_CORDA_ACCOUNT,
            Progress.CREATE_LENDER_STATE,
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

        setCurrentProgressTracker(Progress.CREATE_LENDER_CORDA_ACCOUNT)
        val createAccountTx = subFlow(CreateAccount(name = info.lenderCode))

        setCurrentProgressTracker(Progress.SHARE_LENDER_CORDA_ACCOUNT)
        subFlow(ShareAccountInfo(createAccountTx, info.participants))

        setCurrentProgressTracker(Progress.CREATE_LENDER_STATE)
        val stateData = LenderState.StateData(
            lenderCode = info.lenderCode,
            email = info.email,
            name = info.name,
            active = info.active,
            createdDate = Instant.now(),
            modifiedDate = null
        )
        val output = LenderState(
            stateData = stateData,
            linearId = createAccountTx.state.data.linearId,
            participants = info.participants
        )

        setCurrentProgressTracker(Progress.TX_BUILDING_TRANSACTION)
        val commands = Command(
            value = LenderContract.Commands.CreateLenderAccountCommand(),
            signers = info.participants.map { it.owningKey }
        )
        val txBuilder = TransactionBuilder(notary)
            .addCommand(commands)
            .addOutputState(output, LenderContract.ID)

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

@InitiatedBy(CreateLenderAccountFlow::class)
class CreateLenderAccountFlowResponder(private val counterPartySessions: FlowSession) : FlowLogic<SignedTransaction>() {

    @Suspendable
    override fun call(): SignedTransaction {

        val signTrxFlow = object : SignTransactionFlow(counterPartySessions) {
            override fun checkTransaction(stx: SignedTransaction) {}
        }

        val txId = subFlow(signTrxFlow).id
        return subFlow(ReceiveFinalityFlow(counterPartySessions, expectedTxId = txId))
    }

}