package com.template.webserver.model.rest.request.boardgame

data class ReturnBoardGameRequest(
    val borrowerCode: String,
    val boardGameCodes: List<String>,
    val participants: List<String>
)
