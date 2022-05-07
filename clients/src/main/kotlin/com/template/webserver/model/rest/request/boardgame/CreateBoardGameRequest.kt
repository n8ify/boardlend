package com.template.webserver.model.rest.request.boardgame

import com.template.webserver.constants.enums.BoardGameGenre
import com.template.webserver.constants.enums.BoardGameProductType
import com.template.webserver.constants.enums.BoardGameStatus
import java.time.Instant

data class CreateBoardGameRequest(
    val boardGameCode: String,
    val name: String,
    val description: String,
    val genre: BoardGameGenre,
    val minPlayer: Int,
    val maxPlayer: Int,
    val status: BoardGameStatus,
    val productType: BoardGameProductType,
    val additionalProperties: String,
    val purchasedDate: Instant,
    val participants: List<String>
)
