package com.template.flows.borrower

import co.paralleluniverse.fibers.Suspendable
import com.r3.corda.lib.accounts.workflows.flows.CreateAccount
import com.r3.corda.lib.accounts.workflows.flows.RequestKeyForAccount
import com.r3.corda.lib.accounts.workflows.flows.ShareAccountInfo
import com.r3.corda.lib.accounts.workflows.flows.ShareStateAndSyncAccounts
import com.r3.corda.lib.accounts.workflows.internal.accountService
import com.r3.corda.lib.accounts.workflows.internal.flows.createKeyForAccount
import com.template.contracts.BorrowerContract
import com.template.flows.AbstractFlowLogic
import com.template.info.CreateBorrowerAccountInfo
import com.template.states.BorrowerState
import net.corda.core.contracts.Command
import net.corda.core.flows.*
import net.corda.core.identity.AnonymousParty
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder
import net.corda.core.utilities.ProgressTracker
import java.time.Instant

@InitiatingFlow
@StartableByRPC
class CreateBorrowerAccountFlow(private val info: CreateBorrowerAccountInfo) : AbstractFlowLogic<SignedTransaction>() {

    companion object {
        object Progress {
            object GET_LENDER_CORDA_ACCOUNT: ProgressTracker.Step("CGet particapent lender account")
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
            Progress.GET_LENDER_CORDA_ACCOUNT,
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

        setCurrentProgressTracker(Progress.GET_LENDER_CORDA_ACCOUNT)
        val lenderAccount = serviceHub.accountService.accountInfo(info.participantLenderCode).singleOrNull()
            ?: throw IllegalStateException("Lender participant with code \"${info.participantLenderCode}\" is not found.")
        val lenderKey = subFlow(RequestKeyForAccount(lenderAccount.state.data))
        val lenderAnonymousParty = AnonymousParty(lenderKey.owningKey)

        setCurrentProgressTracker(Progress.CREATE_BORROWER_CORDA_ACCOUNT)
        val createdBorrowerAccountTx = subFlow(CreateAccount(name = info.borrowerCode))
        val createdBorrowerAccountInfo = createdBorrowerAccountTx.state.data
        val createdBorrowerKey = serviceHub.createKeyForAccount(createdBorrowerAccountInfo)
        val createdBorrowerAnonymousParty = AnonymousParty(createdBorrowerKey.owningKey)

        setCurrentProgressTracker(Progress.SHARE_BORROWER_CORDA_ACCOUNT)
        subFlow(ShareAccountInfo(createdBorrowerAccountTx, listOf(lenderAccount.state.data.host)))

        setCurrentProgressTracker(Progress.CREATE_BORROWER_STATE)
        val participants = listOf(createdBorrowerAnonymousParty, lenderAnonymousParty)
        val stateData = BorrowerState.StateData(
            borrowerCode = info.borrowerCode,
            email = info.email,
            tel = info.tel,
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
            linearId = createdBorrowerAccountInfo.linearId,
            participants = participants
        )

        setCurrentProgressTracker(Progress.TX_BUILDING_TRANSACTION)
        val commands = Command(
            value = BorrowerContract.Commands.CreateBorrowerAccountCommand(),
            signers = participants.map { it.owningKey }
        )
        val txBuilder = TransactionBuilder(notary)
            .addCommand(commands)
            .addOutputState(output, BorrowerContract.ID)

        setCurrentProgressTracker(Progress.TX_VERIFICATION)
        txBuilder.verify(serviceHub)

        setCurrentProgressTracker(Progress.SIGS_GATHERING)
        val ptx = serviceHub.signInitialTransaction(txBuilder, createdBorrowerAnonymousParty.owningKey)
        val lenderSession = initiateFlow(lenderAnonymousParty)
        val stx = subFlow(CollectSignaturesFlow( ptx, listOf(lenderSession), listOf(createdBorrowerAnonymousParty.owningKey)))

        val ftx = subFlow(FinalityFlow(stx, listOf(lenderSession), Progress.FINALISATION.childProgressTracker()))
        val state = ftx.tx.outRefsOfType<BorrowerState>().single()

        subFlow(ShareStateAndSyncAccounts(state, lenderAccount.state.data.host))

        return ftx
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