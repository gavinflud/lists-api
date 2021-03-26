package com.gavinflood.lists.api.controller.team

import com.gavinflood.lists.api.controller.dto.ApiResponse
import com.gavinflood.lists.api.service.TeamService
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * Controller with REST endpoints for team management.
 */
@RestController
@RequestMapping("/api/teams")
class TeamController(

    private val teamService: TeamService,
    private val teamRequestMapper: TeamRequestMapper,
    private val teamResponseMapper: TeamResponseMapper

) {

    /**
     * Create a team with the data passed from the [dto] parameter. Once complete, the response should be a
     * [TeamResponseDTO] wrapped in an [ApiResponse].
     *
     * TODO: Add a global function to call [ResponseEntity.ok] passing in an [ApiResponse] to save duplication
     */
    @PostMapping
    fun create(@RequestBody dto: TeamRequestDTO): ResponseEntity<ApiResponse> {
        val team = teamService.create(teamRequestMapper.dtoToEntity(dto))
        return ResponseEntity.ok(ApiResponse(teamResponseMapper.entityToDTO(team)))
    }

    /**
     * Get the teams a particular user (identified by [userId]) is a member of. Results are paginated and in the form
     * of [TeamResponseDTO] wrapped in an [ApiResponse].
     */
    @GetMapping
    fun getMultiple(@RequestParam userId: Long, pageable: Pageable): ResponseEntity<ApiResponse> {
        val pageOfTeams = teamService.findTeamsForUser(userId, pageable)
        val teamDTOs = pageOfTeams.content.map { team -> teamResponseMapper.entityToDTO(team) }
        val pageOfTeamDTOs = PageImpl(teamDTOs, pageable, pageOfTeams.totalElements)
        return ResponseEntity.ok(ApiResponse(pageOfTeamDTOs))
    }

    /**
     * Find a team identified by its [id].
     */
    @GetMapping("/{id}")
    fun getOne(@PathVariable id: Long): ResponseEntity<ApiResponse> {
        val team = teamResponseMapper.entityToDTO(teamService.findById(id))
        return ResponseEntity.ok(ApiResponse(team))
    }

    /**
     * Update a team identified by their [id]. The response should be an [TeamResponseDTO] wrapped in an
     * [ApiResponse].
     */
    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody updatedTeamDTO: TeamRequestDTO): ResponseEntity<ApiResponse> {
        val dtoAsEntity = teamRequestMapper.dtoToEntity(updatedTeamDTO)
        val updatedTeam = teamResponseMapper.entityToDTO(teamService.update(id, dtoAsEntity))
        return ResponseEntity.ok(ApiResponse(updatedTeam))
    }

    /**
     * Retire a team identified by their [id].
     */
    @DeleteMapping("/{id}")
    fun retire(@PathVariable id: Long): ResponseEntity<ApiResponse> {
        return ResponseEntity.ok(ApiResponse(teamService.retire(id)))
    }

}