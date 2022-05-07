package com.template.contracts

import net.corda.core.contracts.CommandData
import net.corda.core.contracts.Contract
import net.corda.core.contracts.ContractState
import net.corda.core.contracts.requireThat
import net.corda.core.transactions.LedgerTransaction

abstract class AbstractBaseContract : Contract {

    fun executeBasicVerifyCommand(
        tx: LedgerTransaction,
        expectedTotalCommand: Int,
        expectedCommandClasses: List<Class<out CommandData>>
    ) {
        requireThat {
            "Command(s) must existed by $expectedTotalCommand command(s), But ${tx.commands.size} found." using (tx.commands.size == expectedTotalCommand)
            "Expected command(s) must equal to expected total command(s). Required ${expectedCommandClasses.size} but ${tx.commands.size} found." using (tx.commands.size == expectedCommandClasses.size)
            "Commands must exactly existed by expected command class(es)" using (tx.commands.map { it.value::class.java }.containsAll(expectedCommandClasses))
        }
    }

    fun executeBasicVerifyInputState(
        tx: LedgerTransaction,
        expectedTotalInputState: Int,
        expectedInputStateClassAndAmount: Map<Class<out ContractState>, Int> = mapOf()
    ) {
        requireThat {
            "Input state(s) must existed by $expectedTotalInputState command(s), But ${tx.inputStates.size} found." using (tx.inputStates.size == expectedTotalInputState)
            expectedInputStateClassAndAmount.forEach { (stateClass, amount) ->
                val txStateByClassCount = tx.inputStates.count { it::class.java == stateClass }
                "Input state(s) type and amount must match the criteria, Required ${stateClass.name} have $txStateByClassCount but $amount is expected" using (txStateByClassCount == amount)
            }
        }
    }

    fun executeBasicVerifyOutputState(
        tx: LedgerTransaction,
        expectedTotalOutputState: Int,
        expectedOutputStateClassAndAmount: Map<Class<out ContractState>, Int>  = mapOf()
    ) {
        requireThat {
            "Output state(s) must existed by $expectedTotalOutputState command(s), But ${tx.outputStates.size} found." using (tx.outputStates.size == expectedTotalOutputState)
            expectedOutputStateClassAndAmount.forEach { (stateClass, amount) ->
                val txStateByClassCount = tx.outputStates.count { it::class.java == stateClass }
                "Output state(s) type and amount must match the criteria, Required ${stateClass.name} have $txStateByClassCount but $amount is expected" using (txStateByClassCount == amount)
            }
        }
    }

}
