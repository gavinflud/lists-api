package com.gavinflood.lists.api.controller.board

import com.gavinflood.lists.api.controller.dto.BasicDTO
import com.gavinflood.lists.api.domain.Board
import com.gavinflood.lists.api.domain.Team

/**
 * DTO for board data sent in requests.
 */
data class BoardRequestDTO(
    val name: String,
    val description: String,
    val teamId: Long,
) : BasicDTO {

    /**
     * Create [Board] from [BoardRequestDTO].
     */
    fun toEntity(team: Team): Board {
        return Board(name, description, team)
    }

}

/**
 * DTO for board data sent in responses.
 */
data class BoardResponseDTO(
    val id: Long,
    val name: String,
    val description: String,
    val teamId: Long
) : BasicDTO

/**
 * Create [BoardResponseDTO] from [Board].
 */
fun Board.toResponseDTO(): BoardResponseDTO {
    return BoardResponseDTO(id, name, description, team.id)
}