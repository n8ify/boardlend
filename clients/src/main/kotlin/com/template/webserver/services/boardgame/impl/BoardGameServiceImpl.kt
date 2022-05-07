package com.template.webserver.services.boardgame.impl

import com.template.flows.borrower.BorrowBoardGameFlow
import com.template.flows.borrower.ReturnBoardGameFlow
import com.template.flows.lender.CreateBoardGameFlow
import com.template.info.BorrowBoardGameInfo
import com.template.info.CreateBoardGameInfo
import com.template.info.ReturnBoardGameInfo
import com.template.states.BoardGameState
import com.template.webserver.constants.enums.ResponseCode
import com.template.webserver.constants.enums.ResponseStatus
import com.template.webserver.model.rest.request.boardgame.BorrowBoardGameRequest
import com.template.webserver.model.rest.request.boardgame.CreateBoardGameRequest
import com.template.webserver.model.rest.request.boardgame.ReturnBoardGameRequest
import com.template.webserver.model.rest.response.CommonResponse
import com.template.webserver.services.AbstractBaseService
import com.template.webserver.services.boardgame.BoardGameService
import net.corda.core.messaging.startFlow
import net.corda.core.messaging.startTrackedFlow
import net.corda.core.utilities.getOrThrow
import org.springframework.stereotype.Service

@Service
class BoardGameServiceImpl : AbstractBaseService(), BoardGameService {

    override fun createBoardGame(request: CreateBoardGameRequest): CommonResponse<BoardGameState.StateData> {

        val info = CreateBoardGameInfo(
            boardGameCode = request.boardGameCode ,
            name = request.name ,
            description = request.description ,
            genre = request.genre.name ,
            minPlayer = request.minPlayer ,
            maxPlayer = request.maxPlayer ,
            status = request.status.name ,
            productType = request.productType.name ,
            additionalProperties = request.additionalProperties ,
            purchasedDate = request.purchasedDate ,
            participants = request.participants.map { obtainPartyByName(it) }
        )

        val result = rpc.proxy.startTrackedFlow(::CreateBoardGameFlow, info)
            .returnValue.getOrThrow().tx.outRefsOfType<BoardGameState>()
        val state = result.single().state.data

        return CommonResponse(
            status = ResponseStatus.Success,
            code = ResponseCode.S00000,
            message = "Create board game \"${state.stateData.name}\" info completed with id ${state.linearId}",
            data = state.stateData
        )

    }

    override fun borrowBoardGame(request: BorrowBoardGameRequest): CommonResponse<List<BoardGameState.StateData>> {

        val info = BorrowBoardGameInfo(
            borrowerCode = request.borrowerCode ,
            boardGameCodes = request.boardGameCodes ,
            lastReturnedDate = request.lastReturnedDate ,
            participants = request.participants.map { obtainPartyByName(it) }
        )

        val result = rpc.proxy.startFlow(::BorrowBoardGameFlow, info)
            .returnValue.getOrThrow().tx.outputsOfType(BoardGameState::class.java)
            .map { it.stateData }

        return CommonResponse(
            status = ResponseStatus.Success,
            code = ResponseCode.S00000,
            message = "Create borrow board game [${result.joinToString (", "){ "\"${it.name} (${it.boardGameCode})\"" }}] info completed ",
            data = result
        )
    }

    override fun returnBoardGame(request: ReturnBoardGameRequest): CommonResponse<List<BoardGameState.StateData>> {

        val info = ReturnBoardGameInfo(
            borrowerCode = request.borrowerCode ,
            boardGameCodes = request.boardGameCodes,
            participants = request.participants.map { obtainPartyByName(it) }
        )

        val result = rpc.proxy.startFlow(::ReturnBoardGameFlow, info)
            .returnValue.getOrThrow().tx.outputsOfType(BoardGameState::class.java)
            .map { it.stateData }

        return CommonResponse(
            status = ResponseStatus.Success,
            code = ResponseCode.S00000,
            message = "Create returned board game [${result.joinToString (", "){ "\"${it.name} (${it.boardGameCode})\"" }}] info completed ",
            data = result
        )
    }
}