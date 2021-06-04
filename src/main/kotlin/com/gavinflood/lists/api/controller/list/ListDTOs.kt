package com.gavinflood.lists.api.controller.list

import com.gavinflood.lists.api.controller.dto.BasicDTO
import com.gavinflood.lists.api.domain.Board
import com.gavinflood.lists.api.domain.List

/**
 * DTO for list data sent in requests.
 */
data class ListRequestDTO(
    val name: String,
    val priority: Int,
    val boardId: Long
) : BasicDTO {

    /**
     * Create [List] from [ListRequestDTO].
     */
    fun toEntity(board: Board): List {
        return List(name, priority, board)
    }

}

/**
 * DTO for list data sent in responses.
 */
data class ListResponseDTO(
    val id: Long,
    val name: String,
    val priority: Int,
    val boardId: Long
) : BasicDTO

/**
 * Create [ListResponseDTO] from [List].
 */
fun List.toResponseDTO(): ListResponseDTO {
    return ListResponseDTO(id, name, priority, board.id)
}

/**
 * DTO for an updated list as part of a multi-update request.
 */
data class UpdateListRequestDTO(
    val id: Long,
    val name: String,
    val priority: Int
) : BasicDTO {

    /**
     * Create [List] from [UpdateListRequestDTO].
     */
    fun toEntity(board: Board): List {
        return List(name, priority, board)
    }

}

/**
 * DTO for updating multiple lists in one request.
 */
data class UpdateMultipleListRequestDTO(
    val lists: Set<UpdateListRequestDTO>
) : BasicDTO