package com.template.info

import net.corda.core.identity.Party
import net.corda.core.serialization.CordaSerializable

@CordaSerializable
data class UpdateBorrowerAccountInfo(
    val linearId: String,
    val email: String?,
    val name: String?,
    val tier: String?,
    val active: Boolean?,
    val participants: List<Party>,
    val version: Int
)