package com.gavinflood.lists.api.controller.user

import com.gavinflood.lists.api.controller.Responses
import com.gavinflood.lists.api.controller.dto.ApiResponse
import com.gavinflood.lists.api.domain.AppUser
import com.gavinflood.lists.api.domain.Credential
import com.gavinflood.lists.api.exception.UsernameAlreadyExistsException
import com.gavinflood.lists.api.service.AppUserService
import com.gavinflood.lists.api.service.CredentialService
import com.gavinflood.lists.api.service.RoleService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * Controller with REST endpoints for user management.
 */
@RestController
@RequestMapping("/api/users")
class AppUserController(

    private val appUserService: AppUserService,
    private val credentialService: CredentialService,
    private val roleService: RoleService

) {

    /**
     * Register a user based on the data passed from the [dto]. This includes creating the associated [Credential]. Once
     * complete, the response should be an [AppUserResponseDTO] wrapped in an [ApiResponse].
     */
    @PostMapping
    fun register(@RequestBody dto: RegisterDTO): ResponseEntity<ApiResponse> {
        return try {
            val credential = credentialService.create(Credential(dto.credential.emailAddress, dto.credential.password))
            val user = appUserService.create(AppUser(dto.user.firstName, dto.user.lastName, credential))
            Responses.ok(user.toResponseDTO())
        } catch (exception: UsernameAlreadyExistsException) {
            ResponseEntity.ok(ApiResponse(ApiResponse.ERROR_CONFLICT, "A user with that email address already exists."))
        }
    }

    /**
     * Find a user based on their [id]. The response should be an [AppUserResponseDTO] wrapped in an [ApiResponse].
     */
    @GetMapping("/{id}")
    fun get(@PathVariable id: Long): ResponseEntity<ApiResponse> {
        val user = appUserService.findById(id)
        return Responses.ok(user.toResponseDTO())
    }

    /**
     * Update a user (identified by their [id]). The response should be an [AppUserResponseDTO] wrapped in an
     * [ApiResponse].
     */
    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody dto: AppUserRequestDTO): ResponseEntity<ApiResponse> {
        val updatedUser = appUserService.update(id, dto.toEntity())
        return Responses.ok(updatedUser.toResponseDTO())
    }

    /**
     * Retire a user (identified by their [id]).
     */
    @DeleteMapping("/{id}")
    fun retire(@PathVariable id: Long): ResponseEntity<ApiResponse> {
        return ResponseEntity.ok(ApiResponse(appUserService.retire(id)))
    }

    /**
     * Update the roles for an individual user (identified by their [id]).
     *
     * TODO: Add custom 403 error handler
     */
    @PutMapping("/{id}/roles")
    fun updateRoles(@PathVariable id: Long, @RequestBody dto: UpdateUserRolesDTO): ResponseEntity<ApiResponse> {
        val roles = roleService.findMultiple(dto.roles)
        return Responses.ok(appUserService.updateRoles(id, roles).toResponseDTO())
    }

}