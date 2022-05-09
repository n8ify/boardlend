package com.template.info

import net.corda.core.identity.Party
import net.corda.core.serialization.CordaSerializable

@CordaSerializable
data class CreateLenderAccountInfo(
    val lenderCode: String,
    val email: String,
    val name: String,
    val active: Boolean,
    val participants: List<Party>
)