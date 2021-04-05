package com.gavinflood.lists.api.controller.board

import com.gavinflood.lists.api.controller.dto.BasicDTO
import com.gavinflood.lists.api.controller.mapper.BasicMapper
import com.gavinflood.lists.api.domain.Board
import org.mapstruct.Mapper
import org.mapstruct.Mapping

/**
 * DTO for board data sent in requests.
 */
data class BoardRequestDTO(
    val name: String,
    val description: String,
    val teamId: Long,
) : BasicDTO

/**
 * Mapper for [Board] entities to [BoardRequestDTO] instances and vice-versa.
 */
@Mapper(componentModel = "spring")
interface BoardRequestMapper : BasicMapper<Board, BoardRequestDTO>

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
 * Mapper for [Board] entities to [BoardResponseDTO] instances and vice-versa.
 */
@Mapper(componentModel = "spring")
interface BoardResponseMapper : BasicMapper<Board, BoardResponseDTO> {

    @Mapping(target = "teamId", source = "team.id")
    override fun entityToDTO(entity: Board): BoardResponseDTO

}