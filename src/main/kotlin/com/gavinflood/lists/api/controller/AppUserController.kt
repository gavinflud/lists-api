package com.gavinflood.lists.api.controller

import com.gavinflood.lists.api.controller.dto.ApiResponse
import com.gavinflood.lists.api.controller.dto.UpdateUserRolesDTO
import com.gavinflood.lists.api.domain.AppUser
import com.gavinflood.lists.api.exception.NoMatchFoundException
import com.gavinflood.lists.api.exception.UsernameAlreadyExistsException
import com.gavinflood.lists.api.service.AppUserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.*

/**
 * Controller with REST endpoints for user management.
 */
@RestController
@RequestMapping("/api/users")
class AppUserController(private val appUserService: AppUserService) {

    /**
     * Register a user.
     *
     * @param user the user to be registered
     * @return the persisted user
     */
    @PostMapping
    fun register(@RequestBody user: AppUser): ResponseEntity<ApiResponse> {
        return try {
            ResponseEntity.ok(ApiResponse(appUserService.create(user)))
        } catch (exception: UsernameAlreadyExistsException) {
            ResponseEntity.ok(ApiResponse(ApiResponse.CONFLICT, "A user with that email address already exists."))
        }
    }

    /**
     * Find a user.
     *
     * @param id identifies the user
     * @return the user if found
     */
    @GetMapping("/{id}")
    fun get(@PathVariable id: Long): ResponseEntity<ApiResponse> {
        return try {
            ResponseEntity.ok(ApiResponse(appUserService.findById(id)))
        } catch (exception: NoMatchFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse(ApiResponse.ERROR_NOT_FOUND, "No user with that ID was found."))
        }
    }

    /**
     * Update a user.
     *
     * @param id identifies the user
     * @param updatedUser the updated representation of the user
     * @return the persisted user
     */
    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody updatedUser: AppUser): ResponseEntity<ApiResponse> {
        return try {
            ResponseEntity.ok(ApiResponse(appUserService.update(id, updatedUser)))
        } catch (exception: NoMatchFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse(ApiResponse.ERROR_NOT_FOUND, "No user with that ID was found."))
        }
    }

    /**
     * Retire a user.
     *
     * @param id identifies the user
     */
    @DeleteMapping("/{id}")
    fun retire(@PathVariable id: Long): ResponseEntity<ApiResponse> {
        return try {
            ResponseEntity.ok(ApiResponse(appUserService.retire(id)))
        } catch (exception: NoMatchFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse(ApiResponse.ERROR_NOT_FOUND, "No user with that ID was found."))
        }
    }

    /**
     * Update the roles for an individual user.
     *
     * @param id identifies the user
     * @param dto stores the new set of roles to be set for the user
     */
    @PutMapping("/{id}/roles")
    @Secured("ROLE_ADMIN")
    fun updateRoles(@PathVariable id: Long, @RequestBody dto: UpdateUserRolesDTO): ResponseEntity<ApiResponse> {
        return try {
            ResponseEntity.ok(ApiResponse(appUserService.updateRoles(id, dto.roles)))
        } catch (exception: NoMatchFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse(ApiResponse.ERROR_NOT_FOUND, "No user with that ID was found."))
        }
    }

}