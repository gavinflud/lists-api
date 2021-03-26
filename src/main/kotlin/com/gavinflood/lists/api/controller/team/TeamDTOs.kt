package com.gavinflood.lists.api.controller.team

import com.gavinflood.lists.api.controller.dto.BasicDTO
import com.gavinflood.lists.api.controller.mapper.BasicMapper
import com.gavinflood.lists.api.domain.Team
import org.mapstruct.Mapper

/**
 * DTO for team data sent in requests.
 */
data class TeamRequestDTO(val name: String) : BasicDTO

/**
 * Mapper for [Team] entities to [TeamRequestDTO] instances and vice-versa.
 */
@Mapper(componentModel = "spring")
interface TeamRequestMapper : BasicMapper<Team, TeamRequestDTO>

/**
 * DTO for team data sent in responses.
 */
data class TeamResponseDTO(
    val id: Long,
    val name: String,
) : BasicDTO

/**
 * Mapper for [Team] entities to [TeamResponseDTO] instances and vice-versa.
 */
@Mapper(componentModel = "spring")
interface TeamResponseMapper : BasicMapper<Team, TeamResponseDTO>