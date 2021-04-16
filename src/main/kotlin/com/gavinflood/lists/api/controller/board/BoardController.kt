package com.gavinflood.lists.api.controller.board

import com.gavinflood.lists.api.controller.Responses
import com.gavinflood.lists.api.controller.dto.ApiResponse
import com.gavinflood.lists.api.domain.Board
import com.gavinflood.lists.api.service.BoardService
import com.gavinflood.lists.api.service.TeamService
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * Controller with REST endpoints for board management.
 */
@RestController
@RequestMapping("/api/boards")
class BoardController(

    private val boardService: BoardService,
    private val teamService: TeamService

) {

    /**
     * Create a [Board].
     */
    @PostMapping
    fun create(@RequestBody dto: BoardRequestDTO): ResponseEntity<ApiResponse> {
        val team = teamService.findById(dto.teamId)
        val board = boardService.create(dto.toEntity(team))
        return Responses.ok(board.toResponseDTO())
    }

    /**
     * Get the boards a particular user (identified by [userId]) is a member of. Results are paginated and in the form
     * of [BoardResponseDTO] wrapped in an [ApiResponse].
     */
    @GetMapping
    fun getMultiple(@RequestParam userId: Long, pageable: Pageable): ResponseEntity<ApiResponse> {
        val pageOfBoards = boardService.findBoardsForUser(userId, pageable)
        val boardDTOs = pageOfBoards.content.map { board -> board.toResponseDTO() }
        val pageOfBoardDTOs = PageImpl(boardDTOs, pageable, pageOfBoards.totalElements)
        return Responses.ok(pageOfBoardDTOs)
    }

    /**
     * Find a board identified by its [id].
     */
    @GetMapping("/{id}")
    fun getOne(@PathVariable id: Long): ResponseEntity<ApiResponse> {
        val board = boardService.findById(id)
        return Responses.ok(board.toResponseDTO())
    }

    /**
     * Update a board identified by its [id]. The response should be an [BoardResponseDTO] wrapped in an [ApiResponse].
     */
    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody updatedBoardDTO: BoardRequestDTO): ResponseEntity<ApiResponse> {
        val team = teamService.findById(updatedBoardDTO.teamId)
        val updatedBoard = boardService.update(id, updatedBoardDTO.toEntity(team))
        return Responses.ok(updatedBoard.toResponseDTO())
    }

    /**
     * Retire a board identified by its [id].
     */
    @DeleteMapping("/{id}")
    fun retire(@PathVariable id: Long): ResponseEntity<ApiResponse> {
        return ResponseEntity.ok(ApiResponse(boardService.retire(id)))
    }

}