package com.gavinflood.lists.api.controller.user

import com.fasterxml.jackson.annotation.JsonProperty
import com.gavinflood.lists.api.controller.dto.BasicDTO
import com.gavinflood.lists.api.domain.AppUser
import com.gavinflood.lists.api.domain.Credential
import com.gavinflood.lists.api.domain.Role

/**
 * DTO for user data sent in requests.
 */
data class AppUserRequestDTO(
    val firstName: String,
    val lastName: String,
) : BasicDTO {

    /**
     * Create a [AppUser] from [AppUserRequestDTO].
     */
    fun toEntity(): AppUser {
        return AppUser(firstName, lastName, Credential("", ""))
    }

}

/**
 * DTO for user data sent in responses.
 */
data class AppUserResponseDTO(
    val id: Long,
    val firstName: String,
    val lastName: String,
) : BasicDTO

/**
 * Create [AppUserResponseDTO] from [AppUser].
 */
fun AppUser.toResponseDTO(): AppUserResponseDTO {
    return AppUserResponseDTO(id, firstName, lastName)
}

/**
 * DTO for credential data sent in requests.
 */
data class CredentialDTO(
    val emailAddress: String,

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    val password: String,
) : BasicDTO

/**
 * DTO used for requests to register a user.
 */
data class RegisterDTO(
    val user: AppUserRequestDTO,
    val credential: CredentialDTO,
) : BasicDTO

/**
 * DTO used for requests to update the roles for a user.
 *
 * TODO: Create a role DTO
 */
data class UpdateUserRolesDTO(val roles: Set<Role>) : BasicDTO