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
     * Check if the current authenticated user ([authentication]) matches the [id] passed in.
     */
    fun isSameUser(authentication: Authentication, id: Long): Boolean {
        val principal = authentication.principal
        if (principal is AppUser) {
            return principal.id == id
        }

        return false
    }

    /**
     * Check if [user] is an administrator (i.e. has the "admin" permission).
     */
    fun isAdmin(user: AppUser): Boolean {
        return user.authorities.map { it.authority }.contains(preloadProperties.permissionAdminCode)
    }

    /**
     * Check if the current authenticated user ([authentication]) is an administrator or matches the [id] passed in.
     */
    fun isAdminOrSameUser(authentication: Authentication, id: Long): Boolean {
        return authentication.authorities.map { it.authority }.contains(preloadProperties.permissionAdminCode)
                || isSameUser(authentication, id)
    }

}