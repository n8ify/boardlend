package com.template.domain

import net.corda.core.schemas.PersistentState
import net.corda.core.schemas.PersistentStateRef
import net.corda.core.serialization.CordaSerializable

@CordaSerializable
data class TrxReference(val outputIndex: Int, val transactionId: String) {

    /** Convert a model to net.corda.core.schemas.PersistentStateRef */
    fun toPersistentState(): PersistentStateRef {
        return PersistentStateRef(txId = transactionId, index = outputIndex)
    }

}
