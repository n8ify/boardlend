package com.template.contracts

import com.template.states.BorrowerState
import net.corda.core.contracts.CommandData
import net.corda.core.transactions.LedgerTransaction

class BorrowerContract : AbstractBaseContract() {

    companion object {
        const val ID = "com.template.contracts.BorrowerContract"
    }

    interface Commands : CommandData {
        class CreateBorrowerAccountCommand: Commands
        class UpdateBorrowerAccountCommand: Commands
    }

    override fun verify(tx: LedgerTransaction) {
        val command = tx.commands.single()
        when (command.value) {
            is Commands.CreateBorrowerAccountCommand -> verifyCreateBorrowerAccountCommand(tx)
            is Commands.UpdateBorrowerAccountCommand -> verifyUpdateBorrowerAccountCommand(tx)
        }
    }

    private fun verifyCreateBorrowerAccountCommand(tx: LedgerTransaction) {

        executeBasicVerifyInputState(
            tx = tx,
            expectedTotalInputState = 0
        )

        executeBasicVerifyOutputState(
            tx = tx,
            expectedTotalOutputState = 1,
            expectedOutputStateClassAndAmount = mapOf(BorrowerState::class.java to 1)
        )

    }

    private fun verifyUpdateBorrowerAccountCommand(tx: LedgerTransaction) {

        executeBasicVerifyInputState(
            tx = tx,
            expectedTotalInputState = 1,
            expectedInputStateClassAndAmount = mapOf(BorrowerState::class.java to 1)
        )

        executeBasicVerifyOutputState(
            tx = tx,
            expectedTotalOutputState = 1,
            expectedOutputStateClassAndAmount = mapOf(BorrowerState::class.java to 1)
        )

    }

}