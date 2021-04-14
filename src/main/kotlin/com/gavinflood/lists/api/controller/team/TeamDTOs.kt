package com.gavinflood.lists.api.controller.team

import com.gavinflood.lists.api.controller.dto.BasicDTO
import com.gavinflood.lists.api.domain.Team

/**
 * DTO for team data sent in requests.
 */
data class TeamRequestDTO(val name: String) : BasicDTO {

    /**
     * Create [Team] from [TeamRequestDTO].
     */
    fun toEntity(): Team {
        return Team(name)
    }

}

/**
 * DTO for team data sent in responses.
 */
data class TeamResponseDTO(
    val id: Long,
    val name: String,
) : BasicDTO

/**
 * Create [TeamResponseDTO] from [Team].
 */
fun Team.toResponseDTO(): TeamResponseDTO {
    return TeamResponseDTO(id, name)
}