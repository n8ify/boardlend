package com.template.contracts

import com.template.states.BoardGameState
import com.template.states.BorrowerState
import com.template.webserver.constants.enums.BoardGameStatus
import net.corda.core.contracts.CommandData
import net.corda.core.contracts.requireThat
import net.corda.core.transactions.LedgerTransaction

class BoardGameContract : AbstractBaseContract() {

    companion object {
        const val ID = "com.template.contracts.BoardGameContract"
    }

    interface Commands: CommandData {
        class CreateBoardGameCommand: Commands
        class BorrowBoardGameCommand: Commands
        class ReturnBoardGameCommand: Commands
    }

    override fun verify(tx: LedgerTransaction) {
        val command = tx.commands.single()
        when (command.value) {
            is Commands.CreateBoardGameCommand -> verifyCreateBoardGameCommand(tx)
            is Commands.BorrowBoardGameCommand -> verifyBorrowBoardGameCommand(tx)
            is Commands.ReturnBoardGameCommand -> verifyReturnBoardGameCommand(tx)
        }
    }

    private fun verifyCreateBoardGameCommand(tx: LedgerTransaction) {

        executeBasicVerifyInputState(
            tx = tx,
            expectedTotalInputState = 0
        )

        executeBasicVerifyOutputState(
            tx = tx,
            expectedTotalOutputState = 1,
            expectedOutputStateClassAndAmount = mapOf(BoardGameState::class.java to 1)
        )

    }
    private fun verifyBorrowBoardGameCommand(tx: LedgerTransaction) {

        val borrowerInputStates = tx.inputsOfType<BorrowerState>()
        val borrowerOutputStates = tx.outputsOfType<BorrowerState>()
        val boardGameInputStates = tx.inputsOfType<BoardGameState>()
        val boardGameOutputStates = tx.outRefsOfType<BoardGameState>()

        requireThat {
            "Must have single borrower state input" using (borrowerInputStates.size == 1)
            "Must have single borrower state output" using (borrowerOutputStates.size == 1)
            "Borrower must be active" using (borrowerInputStates.single().stateData.active && borrowerOutputStates.single().stateData.active)
            "Borrower's borrowing input state must be on borrowing" using (!borrowerInputStates.single().stateData.isBorrowing)
            "Borrower's borrowing output state must be on borrowing" using (borrowerOutputStates.single().stateData.isBorrowing)
            "Input Board game must be borrowable status" using (boardGameInputStates.all { it.stateData.status == BoardGameStatus.Borrowable.name })
            "Output Board game must be borrowing status" using (boardGameOutputStates.all { it.state.data.stateData.status == BoardGameStatus.Borrowing.name })
            "Both Board game input state(s) and output state(s) must have at least 1 state" using (boardGameInputStates.isNotEmpty() && boardGameOutputStates.isNotEmpty())
            "Board game input state(s) must equal output state(s)" using (boardGameInputStates.size == boardGameOutputStates.size)
        }

    }

    private fun verifyReturnBoardGameCommand(tx: LedgerTransaction) {

        val borrowerInputStates = tx.inputsOfType<BorrowerState>()
        val borrowerOutputStates = tx.outputsOfType<BorrowerState>()
        val boardGameInputStates = tx.inputsOfType<BoardGameState>()
        val boardGameOutputStates = tx.outRefsOfType<BoardGameState>()

        requireThat {
            "Must have single borrower state input" using (borrowerInputStates.size == 1)
            "Must have single borrower state output" using (borrowerOutputStates.size == 1)
            "Borrower must be active" using (borrowerInputStates.single().stateData.active && borrowerOutputStates.single().stateData.active)
            "Borrower's borrowing input state must be on borrowing" using (borrowerInputStates.single().stateData.isBorrowing)
            "Borrower's borrowing output state must be on borrowing" using (!borrowerOutputStates.single().stateData.isBorrowing)
            "Input Board game must be borrowing status" using (boardGameInputStates.all { it.stateData.status == BoardGameStatus.Borrowing.name })
            "Output Board game must be borrowable status" using (boardGameOutputStates.all { it.state.data.stateData.status == BoardGameStatus.Borrowable.name })
            "Both Board game input state(s) and output state(s) must have at least 1 state" using (boardGameInputStates.isNotEmpty() && boardGameOutputStates.isNotEmpty())
            "Board game input state(s) must equal output state(s)" using (boardGameInputStates.size == boardGameOutputStates.size)
        }

    }


}