package com.template.webserver.model.rest.request.boardgame

import java.time.Instant

data class BorrowBoardGameRequest(
    val borrowerCode: String,
    val boardGameCodes: List<String>,
    val mustReturnedDate: Instant,
    val participants: List<String>
)
