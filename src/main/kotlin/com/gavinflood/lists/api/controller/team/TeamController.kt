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
class TeamController(private val teamService: TeamService) {

    /**
     * Create a team with the data passed from the [dto] parameter. Once complete, the response should be a
     * [TeamResponseDTO] wrapped in an [ApiResponse].
     *
     * TODO: Add a global function to call [ResponseEntity.ok] passing in an [ApiResponse] to save duplication
     */
    @PostMapping
    fun create(@RequestBody dto: TeamRequestDTO): ResponseEntity<ApiResponse> {
        val team = teamService.create(dto.toEntity())
        return ResponseEntity.ok(ApiResponse(team.toResponseDTO()))
    }

    /**
     * Get the teams a particular user (identified by [userId]) is a member of. Results are paginated and in the form
     * of [TeamResponseDTO] wrapped in an [ApiResponse].
     */
    @GetMapping
    fun getMultiple(@RequestParam userId: Long, pageable: Pageable): ResponseEntity<ApiResponse> {
        val pageOfTeams = teamService.findTeamsForUser(userId, pageable)
        val teamDTOs = pageOfTeams.content.map { team -> team.toResponseDTO() }
        val pageOfTeamDTOs = PageImpl(teamDTOs, pageable, pageOfTeams.totalElements)
        return ResponseEntity.ok(ApiResponse(pageOfTeamDTOs))
    }

    /**
     * Find a team identified by its [id].
     */
    @GetMapping("/{id}")
    fun getOne(@PathVariable id: Long): ResponseEntity<ApiResponse> {
        val team = teamService.findById(id)
        return ResponseEntity.ok(ApiResponse(team.toResponseDTO()))
    }

    /**
     * Update a team identified by their [id]. The response should be an [TeamResponseDTO] wrapped in an
     * [ApiResponse].
     */
    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody updatedTeamDTO: TeamRequestDTO): ResponseEntity<ApiResponse> {
        val updatedTeam = teamService.update(id, updatedTeamDTO.toEntity())
        return ResponseEntity.ok(ApiResponse(updatedTeam.toResponseDTO()))
    }

    /**
     * Retire a team identified by their [id].
     */
    @DeleteMapping("/{id}")
    fun retire(@PathVariable id: Long): ResponseEntity<ApiResponse> {
        return ResponseEntity.ok(ApiResponse(teamService.retire(id)))
    }

}