package com.gavinflood.lists.api.controller.team

import com.gavinflood.lists.api.controller.Responses
import com.gavinflood.lists.api.controller.dto.ApiResponse
import com.gavinflood.lists.api.controller.user.toResponseDTO
import com.gavinflood.lists.api.domain.AppUser
import com.gavinflood.lists.api.service.AppUserService
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
    private val appUserService: AppUserService

) {

    /**
     * Create a team with the data passed from the [dto] parameter. Once complete, the response should be a
     * [TeamResponseDTO] wrapped in an [ApiResponse].
     */
    @PostMapping
    fun create(@RequestBody dto: TeamRequestDTO): ResponseEntity<ApiResponse> {
        val team = teamService.create(dto.toEntity())
        return Responses.ok(team.toResponseDTO())
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
        return Responses.ok(pageOfTeamDTOs)
    }

    /**
     * Find a team identified by its [id].
     */
    @GetMapping("/{id}")
    fun getOne(@PathVariable id: Long): ResponseEntity<ApiResponse> {
        val team = teamService.findById(id)
        return Responses.ok(team.toResponseDTO())
    }

    /**
     * Update a team identified by their [id]. The response should be an [TeamResponseDTO] wrapped in an
     * [ApiResponse].
     */
    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody updatedTeamDTO: TeamRequestDTO): ResponseEntity<ApiResponse> {
        val updatedTeam = teamService.update(id, updatedTeamDTO.toEntity())
        return Responses.ok(updatedTeam.toResponseDTO())
    }

    /**
     * Retire a team identified by their [id].
     */
    @DeleteMapping("/{id}")
    fun retire(@PathVariable id: Long): ResponseEntity<ApiResponse> {
        return ResponseEntity.ok(ApiResponse(teamService.retire(id)))
    }

    /**
     * Adds one or more users to the team identified by its [id].
     */
    @PutMapping("/{id}/members")
    fun addMembers(@PathVariable id: Long, @RequestBody teamMembersDTO: TeamMembersDTO): ResponseEntity<ApiResponse> {
        val users = teamMembersDTO.members.map { appUserService.loadUserByUsername(it.username) as AppUser }
        val addedUsers = teamService.addMembers(id, users)
        return Responses.ok(addedUsers.map { it.toResponseDTO() })
    }

    /**
     * Removes one or more users from the team identified by its [id].
     */
    @DeleteMapping("/{id}/members")
    fun removeMembers(
        @PathVariable id: Long,
        @RequestBody teamMembersDTO: TeamMembersDTO
    ): ResponseEntity<ApiResponse> {
        val users = teamMembersDTO.members.map { appUserService.loadUserByUsername(it.username) as AppUser }
        val addedUsers = teamService.removeMembers(id, users)
        return Responses.ok(addedUsers.map { it.toResponseDTO() })
    }

    /**
     * Get the members of a team identified by its [id].
     *
     * TODO: Might be better to paginate this or combine it with the [getOne] DTO.
     */
    @GetMapping("/{id}/members")
    fun getMembers(@PathVariable id: Long): ResponseEntity<ApiResponse> {
        val memberDTOs = teamService.findById(id).members.map { user -> user.toResponseDTO() }
        return Responses.ok(memberDTOs)
    }

}