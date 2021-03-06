package com.template.info

import net.corda.core.identity.Party
import net.corda.core.serialization.CordaSerializable

@CordaSerializable
data class CreateBorrowerAccountInfo(
    val borrowerCode: String,
    val email: String,
    val tel: String,
    val name: String,
    val tier: String,
    val active: Boolean,
    val participantLenderCode: String
)