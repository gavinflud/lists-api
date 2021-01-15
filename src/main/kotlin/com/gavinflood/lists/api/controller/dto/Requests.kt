package com.gavinflood.lists.api.controller.dto

import com.gavinflood.lists.api.domain.Role

/**
 * DTO used for requests to update the roles for a user.
 *
 * @param roles the new set of roles the user should have
 */
data class UpdateUserRolesDTO(val roles: Set<Role>)