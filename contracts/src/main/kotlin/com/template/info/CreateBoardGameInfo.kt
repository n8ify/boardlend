package com.template.info

import net.corda.core.identity.Party
import net.corda.core.serialization.CordaSerializable
import java.time.Instant

@CordaSerializable
data class CreateBoardGameInfo (
    val boardGameCode: String,
    val name: String,
    val description: String,
    val genre: String,
    val minPlayer: Int,
    val maxPlayer: Int,
    val status: String,
    val productType: String,
    val additionalProperties: String,
    val purchasedDate: Instant,
    val lenderId: String,
    val participants: List<Party>
)
