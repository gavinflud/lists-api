package com.gavinflood.lists.api.service

import com.gavinflood.lists.api.data.PreloadProperties
import com.gavinflood.lists.api.domain.AppUser
import com.gavinflood.lists.api.domain.Role
import com.gavinflood.lists.api.exception.NoMatchFoundException
import com.gavinflood.lists.api.repository.AppUserRepository
import org.slf4j.LoggerFactory
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

/**
 * Business logic for application users.
 */
@Service
class AppUserService(

    private val appUserRepository: AppUserRepository,
    private val credentialService: CredentialService,
    private val roleService: RoleService,
    private val preloadProperties: PreloadProperties,

    ) : UserDetailsService {

    private val logger = LoggerFactory.getLogger(AppUserService::class.java)

    /**
     * Load a user based on their [username]. This involves a query for the credential followed by a query for the user.
     */
    override fun loadUserByUsername(username: String): UserDetails {
        val credential = credentialService.findOne(username)
        val user = appUserRepository.findDistinctByCredentialAndRetiredIsFalse(credential)

        if (user.isPresent) {
            return user.get()
        }

        throw UsernameNotFoundException("No user found with username '$username'")
    }

    /**
     * Create a new [user] and return the saved [AppUser].
     */
    fun create(user: AppUser): AppUser {
        // All new users should get the default "user" role
        if (user.roles.isEmpty()) {
            user.roles.add(roleService.findByCode(preloadProperties.roleUserCode))
        }

        return appUserRepository.save(user)
    }

    /**
     * Find a user by their unique [id].
     */
    fun findById(id: Long): AppUser {
        val user = appUserRepository.findById(id)

        if (user.isEmpty) {
            logger.warn("No user was found with ID '$id'")
            throw NoMatchFoundException("No user was found with ID '$id'")
        }

        return user.get()
    }

    /**
     * Update an existing user (identified by their unique [id]. This does not allow the user to update their
     * credentials. Returns the updated [AppUser].
     */
    @PreAuthorize("@userSecurity.isAdminOrSameUser(authentication, #id)")
    fun update(id: Long, updatedUser: AppUser): AppUser {
        try {
            val user = findById(id)
            user.firstName = updatedUser.firstName
            user.lastName = updatedUser.lastName
            logger.info("Updating user ${user.id}")
            return appUserRepository.save(user)
        } catch (exception: NoMatchFoundException) {
            logger.warn("Cannot update a user as none exists with the ID '$id'")
            throw exception
        }
    }

    /**
     * Retire an existing user (identified by their unique [id]).
     */
    @PreAuthorize("@userSecurity.isAdminOrSameUser(authentication, #id)")
    fun retire(id: Long) {
        try {
            val user = findById(id)
            user.retire()
            logger.info("Retiring user ${user.id}")
            appUserRepository.save(user)
        } catch (exception: NoMatchFoundException) {
            logger.warn("Cannot retire a user as none exists with the ID '$id'")
            throw exception
        }
    }

    /**
     * Update the roles assigned to a user (identified by their unique [id]). This clears the user's current roles and
     * assigns the passed [roles].
     */
    @PreAuthorize("hasAuthority('admin')")
    fun updateRoles(id: Long, roles: Set<Role>): AppUser {
        try {
            val user = findById(id)
            user.roles.clear()
            user.roles.addAll(roleService.findMultiple(roles.map { it.code }.toSet()))
            logger.info("Updating user '${user.id}' to have roles [${user.roles.joinToString { it.code }}]")
            return appUserRepository.save(user)
        } catch (exception: NoMatchFoundException) {
            logger.warn("Cannot update roles for user as none exists with the ID '$id'")
            throw exception
        }
    }

    /**
     * Get the current authenticated [AppUser].
     */
    fun getCurrentAuthenticatedUser(): AppUser {
        val currentUsername = SecurityContextHolder.getContext().authentication.name
        return loadUserByUsername(currentUsername) as AppUser
    }

}