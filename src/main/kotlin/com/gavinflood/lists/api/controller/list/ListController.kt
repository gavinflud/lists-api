package com.gavinflood.lists.api.controller.list

import com.gavinflood.lists.api.controller.Responses
import com.gavinflood.lists.api.controller.dto.ApiResponse
import com.gavinflood.lists.api.service.BoardService
import com.gavinflood.lists.api.service.ListService
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * Controller with REST endpoints for list management.
 */
@RestController
@RequestMapping("/api/lists")
class ListController(

    private val listService: ListService,
    private val boardService: BoardService

) {

    /**
     * Create a [List].
     */
    @PostMapping
    fun create(@RequestBody dto: ListRequestDTO): ResponseEntity<ApiResponse> {
        val board = boardService.findById(dto.boardId)
        val list = listService.create(dto.toEntity(board))
        return Responses.ok(list.toResponseDTO())
    }

    /**
     * Get the lists under a particular board (identified by [boardId]). Results are paginated and in the form
     * of [ListResponseDTO] wrapped in an [ApiResponse].
     */
    @GetMapping
    fun getMultiple(@RequestParam boardId: Long, pageable: Pageable): ResponseEntity<ApiResponse> {
        val board = boardService.findById(boardId)
        val lists = listService.findAllUnderBoard(board)
        return Responses.ok(lists.map { list -> list.toResponseDTO() })
    }

    /**
     * Find a list identified by its [id].
     */
    @GetMapping("/{id}")
    fun getOne(@PathVariable id: Long): ResponseEntity<ApiResponse> {
        val list = listService.findById(id)
        return Responses.ok(list.toResponseDTO())
    }

    /**
     * Update a list identified by its [id]. The response should be an [ListResponseDTO] wrapped in an [ApiResponse].
     */
    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody updatedListDTO: ListRequestDTO): ResponseEntity<ApiResponse> {
        val board = boardService.findById(updatedListDTO.boardId)
        val updatedList = listService.update(id, updatedListDTO.toEntity(board))
        return Responses.ok(updatedList.toResponseDTO())
    }

    /**
     * Retire a list identified by its [id].
     */
    @DeleteMapping("/{id}")
    fun retire(@PathVariable id: Long): ResponseEntity<ApiResponse> {
        return ResponseEntity.ok(ApiResponse(listService.retire(id)))
    }

}