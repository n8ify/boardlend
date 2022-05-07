package com.template.info

import net.corda.core.identity.Party
import net.corda.core.serialization.CordaSerializable
import java.time.Instant

@CordaSerializable
data class ReturnBoardGameInfo (
    val borrowerCode: String,
    val boardGameCodes: List<String>,
    val participants: List<Party>
)
