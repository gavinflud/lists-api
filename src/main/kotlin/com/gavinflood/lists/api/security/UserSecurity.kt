package com.gavinflood.lists.api.security

import com.gavinflood.lists.api.data.PreloadProperties
import com.gavinflood.lists.api.domain.AppUser
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component

/**
 * Common user security functionality.
 */
@Component
class UserSecurity(private val preloadProperties: PreloadProperties) {

    /**
     * Check if the current authenticated user matches the ID passed in.
     *
     * @param authentication the current authentication
     * @param id the user ID to check against
     * @return true if the user matches, false otherwise
     */
    fun isSameUser(authentication: Authentication, id: Long): Boolean {
        val principal = authentication.principal
        if (principal is AppUser) {
            return principal.id == id
        }

        return false
    }

    /**
     * Check if a user is an administrator.
     *
     * @param user the user to check
     * @return true if they have the admin permission and false otherwise
     */
    fun isAdmin(user: AppUser): Boolean {
        return user.authorities.map { it.authority }.contains(preloadProperties.permissionAdminCode)
    }

    /**
     * Check if the current authenticated user is an administrator or matches the ID passed in.
     *
     * @param authentication the current authentication
     * @param id the user ID to check against
     * @return true if the user matches, false otherwise
     */
    fun isAdminOrSameUser(authentication: Authentication, id: Long): Boolean {
        return authentication.authorities.map { it.authority }.contains(preloadProperties.permissionAdminCode)
            || isSameUser(authentication, id)
    }

}