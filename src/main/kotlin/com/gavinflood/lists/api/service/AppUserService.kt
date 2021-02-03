package com.gavinflood.lists.api.service

import com.gavinflood.lists.api.data.PreloadProperties
import com.gavinflood.lists.api.domain.AppUser
import com.gavinflood.lists.api.domain.Role
import com.gavinflood.lists.api.exception.NoMatchFoundException
import com.gavinflood.lists.api.exception.UsernameAlreadyExistsException
import com.gavinflood.lists.api.repository.AppUserRepository
import com.gavinflood.lists.api.repository.CredentialRepository
import org.slf4j.LoggerFactory
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

/**
 * Business logic for application users.
 */
@Service
class AppUserService(

    private val appUserRepository: AppUserRepository,
    private val credentialRepository: CredentialRepository,
    private val roleService: RoleService,
    private val preloadProperties: PreloadProperties,
    private val passwordEncoder: PasswordEncoder

) : UserDetailsService {

    private val logger = LoggerFactory.getLogger(AppUserService::class.java)

    /**
     * Load a user based on their username. This involves a query for the credential followed by a query for the user.
     *
     * @param username of the user
     * @return a [UserDetails] implementation for the user
     */
    override fun loadUserByUsername(username: String): UserDetails {
        val credential = credentialRepository.findDistinctByEmailAddress(username)
        var usernameFound = credential.isPresent

        if (usernameFound) {
            val user = appUserRepository.findDistinctByCredentialAndRetiredIsFalse(credential.get())
            usernameFound = user.isPresent

            if (usernameFound) {
                return user.get()
            }
        }

        throw UsernameNotFoundException("No user found with username '$username'")
    }

    /**
     * Create a new user.
     *
     * @param user the user to be created
     * @return the persisted user
     * @throws UsernameAlreadyExistsException if the username is already in use by another user
     */
    fun create(user: AppUser): AppUser {
        val existingCredential = credentialRepository.findDistinctByEmailAddress(user.credential.emailAddress)
        if (existingCredential.isPresent) {
            logger.info("Cannot create a user with a username currently used by user '${existingCredential.get().id}'")
            throw UsernameAlreadyExistsException()
        }

        user.credential.password = passwordEncoder.encode(user.credential.password)

        // All new users should get the default "user" role
        if (user.roles.isEmpty()) {
            user.roles.add(roleService.findByCode(preloadProperties.roleUserCode))
        }

        return appUserRepository.save(user)
    }

    /**
     * Find a user by their unique ID.
     *
     * @param id identifies the user
     * @return the user if they exist
     * @throws NoMatchFoundException if a user with that ID was not found
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
     * Update an existing user. This does not allow the user to update their credentials.
     *
     * @param id identifies the user
     * @param updatedUser the updated user details
     * @return the persisted user
     * @throws NoMatchFoundException if a user with that ID was not found
     */
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
     * Retire an existing user.
     *
     * @param id identifies the user
     * @throws NoMatchFoundException if a user with that ID was not found
     */
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
     * Update the roles assigned to a user.
     *
     * @param id identifies the user
     * @param roles the complete set of roles the user should have
     * @return the updated user
     * @throws NoMatchFoundException if a user with that ID was not found
     */
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
     * @return the current authenticated user
     */
    fun getCurrentAuthenticatedUser(): AppUser {
        val currentUsername = SecurityContextHolder.getContext().authentication.name
        return loadUserByUsername(currentUsername) as AppUser
    }

}