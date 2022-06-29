package com.template.info

import net.corda.core.identity.Party
import net.corda.core.serialization.CordaSerializable
import java.time.Instant

@CordaSerializable
data class BorrowBoardGameInfo (
    val borrowerCode: String,
    val boardGameCodes: List<String>,
    val mustReturnedDate: Instant,
    val participants: List<Party>
)
