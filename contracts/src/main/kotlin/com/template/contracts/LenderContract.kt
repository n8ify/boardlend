package com.template.contracts

import com.template.states.LenderState
import net.corda.core.contracts.CommandData
import net.corda.core.transactions.LedgerTransaction

class LenderContract : AbstractBaseContract() {

    companion object {
        const val ID = "com.template.contracts.LenderContract"
    }

    interface Commands : CommandData {
        class CreateLenderAccountCommand: Commands
        class UpdateLenderAccountCommand: Commands
    }

    override fun verify(tx: LedgerTransaction) {
        val command = tx.commands.single()
        when (command.value) {
            is Commands.CreateLenderAccountCommand -> verifyCreateLenderAccountCommand(tx)
            is Commands.UpdateLenderAccountCommand -> verifyUpdateLenderAccountCommand(tx)
        }
    }

    private fun verifyCreateLenderAccountCommand(tx: LedgerTransaction) {

        executeBasicVerifyInputState(
            tx = tx,
            expectedTotalInputState = 0
        )

        executeBasicVerifyOutputState(
            tx = tx,
            expectedTotalOutputState = 1,
            expectedOutputStateClassAndAmount = mapOf(LenderState::class.java to 1)
        )

    }

    private fun verifyUpdateLenderAccountCommand(tx: LedgerTransaction) {

        executeBasicVerifyInputState(
            tx = tx,
            expectedTotalInputState = 1,
            expectedInputStateClassAndAmount = mapOf(LenderState::class.java to 1)
        )

        executeBasicVerifyOutputState(
            tx = tx,
            expectedTotalOutputState = 1,
            expectedOutputStateClassAndAmount = mapOf(LenderState::class.java to 1)
        )

    }

}