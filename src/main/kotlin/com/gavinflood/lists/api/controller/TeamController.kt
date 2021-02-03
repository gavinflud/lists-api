package com.gavinflood.lists.api.controller

import com.gavinflood.lists.api.controller.dto.ApiResponse
import com.gavinflood.lists.api.domain.Team
import com.gavinflood.lists.api.exception.NoMatchFoundException
import com.gavinflood.lists.api.service.TeamService
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * Controller with REST endpoints for team management.
 */
@RestController
@RequestMapping("/api/teams")
class TeamController(private val teamService: TeamService) {

    /**
     * Create a team.
     *
     * @param team the team to be created
     * @return the persisted team
     */
    @PostMapping
    fun create(@RequestBody team: Team): ResponseEntity<ApiResponse> {
        return ResponseEntity.ok(ApiResponse(teamService.create(team)))
    }

    /**
     * Get the teams a particular user is a member of. Results are paginated.
     *
     * @param userId identifies the user
     * @param pageable defines the page index and size
     * @return the teams the user is a member of
     */
    @GetMapping
    fun getMultiple(@RequestParam userId: Long, pageable: Pageable): ResponseEntity<ApiResponse> {
        return try {
            ResponseEntity.ok(ApiResponse(teamService.findTeamsForUser(userId, pageable)))
        } catch (exception: NoMatchFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse(ApiResponse.ERROR_NOT_FOUND, "No user with that ID was found."))
        }
    }

    /**
     * Find a team.
     *
     * @param id identifies the team
     * @return the team if found
     */
    @GetMapping("/{id}")
    fun getOne(@PathVariable id: Long): ResponseEntity<ApiResponse> {
        return try {
            ResponseEntity.ok(ApiResponse(teamService.findById(id)))
        } catch (exception: NoMatchFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse(ApiResponse.ERROR_NOT_FOUND, "No team with that ID was found."))
        }
    }

    /**
     * Update a team.
     *
     * @param id identifies the team
     * @param updatedTeam the updated representation of the team
     * @return the persisted team
     */
    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody updatedTeam: Team): ResponseEntity<ApiResponse> {
        return try {
            ResponseEntity.ok(ApiResponse(teamService.update(id, updatedTeam)))
        } catch (exception: NoMatchFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse(ApiResponse.ERROR_NOT_FOUND, "No team with that ID was found."))
        }
    }

    /**
     * Retire a team.
     *
     * @param id identifies the team
     */
    @DeleteMapping("/{id}")
    fun retire(@PathVariable id: Long): ResponseEntity<ApiResponse> {
        return try {
            ResponseEntity.ok(ApiResponse(teamService.retire(id)))
        } catch (exception: NoMatchFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse(ApiResponse.ERROR_NOT_FOUND, "No team with that ID was found."))
        }
    }

}