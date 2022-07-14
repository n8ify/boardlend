package com.template.flows.lender

import co.paralleluniverse.fibers.Suspendable
import com.r3.corda.lib.accounts.workflows.flows.CreateAccount
import com.r3.corda.lib.accounts.workflows.flows.ShareAccountInfo
import com.template.contracts.BorrowerContract
import com.template.contracts.LenderContract
import com.template.flows.AbstractFlowLogic
import com.template.info.CreateBorrowerAccountInfo
import com.template.info.CreateLenderAccountInfo
import com.template.info.UpdateLenderAccountInfo
import com.template.repositories.LenderRepository
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
class UpdateLenderAccountFlow(private val info: UpdateLenderAccountInfo) : AbstractFlowLogic<SignedTransaction>() {

    companion object {
        object Progress {
            object GET_EXISTED_LENDER_ACCOUNT: ProgressTracker.Step("Get existed lender account")
            object MODIFY_LENDER_STATE: ProgressTracker.Step("Modify a lender's state.")
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
            Progress.GET_EXISTED_LENDER_ACCOUNT,
            Progress.MODIFY_LENDER_STATE,
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

        val lenderRepository = serviceHub.cordaService(LenderRepository::class.java)

        setCurrentProgressTracker(Progress.GET_EXISTED_LENDER_ACCOUNT)
        val existedLenderStateAndRef = lenderRepository.getLenderByCode(info.lenderCode)
        val existedLenderState = existedLenderStateAndRef.state.data

        setCurrentProgressTracker(Progress.MODIFY_LENDER_STATE)
        val newLenderState = existedLenderState.modify(info)

        setCurrentProgressTracker(Progress.TX_BUILDING_TRANSACTION)
        val commands = Command(
            value = LenderContract.Commands.UpdateLenderAccountCommand(),
            signers = existedLenderState.participants.map { it.owningKey }
        )
        val txBuilder = TransactionBuilder(notary)
            .addCommand(commands)
            .addInputState(existedLenderStateAndRef)
            .addOutputState(newLenderState, LenderContract.ID)

        setCurrentProgressTracker(Progress.TX_VERIFICATION)
        txBuilder.verify(serviceHub)

        setCurrentProgressTracker(Progress.SIGS_GATHERING)
        val ptx = serviceHub.signInitialTransaction(txBuilder)
        val sessions = existedLenderState.participants.filter { it != ourIdentity }.map { initiateFlow(it) }
        val stx = subFlow(CollectSignaturesFlow( ptx, sessions))

        setCurrentProgressTracker(Progress.FINALISATION)
        return subFlow(FinalityFlow(stx, sessions))
    }

}

@InitiatedBy(UpdateLenderAccountFlow::class)
class UpdateLenderAccountFlowResponder(private val counterPartySessions: FlowSession) : FlowLogic<SignedTransaction>() {

    @Suspendable
    override fun call(): SignedTransaction {

        val signTrxFlow = object : SignTransactionFlow(counterPartySessions) {
            override fun checkTransaction(stx: SignedTransaction) {}
        }

        val txId = subFlow(signTrxFlow).id
        return subFlow(ReceiveFinalityFlow(counterPartySessions, expectedTxId = txId))
    }

}