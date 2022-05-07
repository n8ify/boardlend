package com.template.webserver.controllers

import com.template.states.BoardGameState
import com.template.webserver.model.rest.request.boardgame.BorrowBoardGameRequest
import com.template.webserver.model.rest.request.boardgame.CreateBoardGameRequest
import com.template.webserver.model.rest.request.boardgame.ReturnBoardGameRequest
import com.template.webserver.model.rest.response.CommonResponse
import com.template.webserver.services.boardgame.impl.BoardGameServiceImpl
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/boardgame")
class BoardGameController(private val service: BoardGameServiceImpl) {

    @PostMapping(value = ["/create"], produces = ["application/json"])
    fun create(@RequestBody request: CreateBoardGameRequest): CommonResponse<BoardGameState.StateData> {
        return service.createBoardGame(request)
    }

    @PostMapping(value = ["/borrow"], produces = ["application/json"])
    fun borrow(@RequestBody request: BorrowBoardGameRequest): CommonResponse<List<BoardGameState.StateData>> {
        return service.borrowBoardGame(request)
    }

    @PostMapping(value = ["/return"], produces = ["application/json"])
    fun `return`(@RequestBody request: ReturnBoardGameRequest): CommonResponse<List<BoardGameState.StateData>> {
        return service.returnBoardGame(request)
    }

}