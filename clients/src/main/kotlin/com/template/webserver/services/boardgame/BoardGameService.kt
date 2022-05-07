package com.template.webserver.services.boardgame

import com.template.states.BoardGameState
import com.template.webserver.model.rest.request.boardgame.BorrowBoardGameRequest
import com.template.webserver.model.rest.request.boardgame.CreateBoardGameRequest
import com.template.webserver.model.rest.request.boardgame.ReturnBoardGameRequest
import com.template.webserver.model.rest.response.CommonResponse

interface BoardGameService {

    fun createBoardGame(request: CreateBoardGameRequest): CommonResponse<BoardGameState.StateData>
    fun borrowBoardGame(request: BorrowBoardGameRequest): CommonResponse<List<BoardGameState.StateData>>
    fun returnBoardGame(request: ReturnBoardGameRequest): CommonResponse<List<BoardGameState.StateData>>

}