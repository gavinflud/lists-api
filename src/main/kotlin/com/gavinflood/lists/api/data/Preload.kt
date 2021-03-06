package com.gavinflood.lists.api.data

import com.gavinflood.lists.api.domain.AppUser
import com.gavinflood.lists.api.domain.Credential
import com.gavinflood.lists.api.domain.Permission
import com.gavinflood.lists.api.domain.Role
import com.gavinflood.lists.api.repository.CredentialRepository
import com.gavinflood.lists.api.service.AppUserService
import com.gavinflood.lists.api.service.PermissionService
import com.gavinflood.lists.api.service.RoleService
import org.springframework.context.ApplicationListener
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

/**
 * Preload required data into the database. This is used to load data that is not user-entered and critical to the
 * running of the application.
 */
@Component
class Preload(

    private val preloadProperties: PreloadProperties,
    private val credentialRepository: CredentialRepository,
    private val permissionService: PermissionService,
    private val roleService: RoleService,
    private val userService: AppUserService,
    private val passwordEncoder: PasswordEncoder

) : ApplicationListener<ContextRefreshedEvent> {

    private var hasPreloadAlreadyCompleted = false

    /**
     * Called when the application context is initialised or refreshed.
     */
    override fun onApplicationEvent(event: ContextRefreshedEvent) {
        if (!hasPreloadAlreadyCompleted && !hasPreloadAlreadyCompleted()) {
            preload()
        }
    }

    /**
     * Checks if the preload has already completed by querying for data created during it.
     */
    private fun hasPreloadAlreadyCompleted(): Boolean {
        return credentialRepository.findDistinctByEmailAddress(preloadProperties.userAdminUsername).isPresent
    }

    /**
     * Preload default and admin permissions and roles, as well as the admin user.
     */
    private fun preload() {
        val props = preloadProperties
        val defaultPermission = createPermission(props.permissionDefaultCode, props.permissionDefaultDescription)
        val adminPermission = createPermission(props.permissionAdminCode, props.permissionAdminDescription)
        val userRole = createRole(props.roleUserCode, props.roleUserDescription, mutableSetOf(defaultPermission))
        val adminRole = createRole(
            props.roleAdminCode,
            props.roleAdminDescription,
            mutableSetOf(
                defaultPermission,
                adminPermission
            )
        )

        createUser(
            props.userAdminUsername,
            props.userAdminPassword,
            props.userAdminFirstName,
            props.userAdminLastName,
            mutableSetOf(userRole, adminRole)
        )

        hasPreloadAlreadyCompleted = true
    }

    /**
     * Create a [Permission].
     */
    private fun createPermission(code: String, description: String): Permission {
        return permissionService.create(Permission(code, description))
    }

    /**
     * Create a [Role].
     */
    private fun createRole(code: String, description: String, permissions: MutableSet<Permission>): Role {
        return roleService.create(Role(code, description, permissions))
    }

    /**
     * Create a user[AppUser].
     */
    private fun createUser(
        email: String,
        password: String,
        firstName: String,
        lastName: String,
        roles: MutableSet<Role>
    ): AppUser {
        val credential = Credential(email, passwordEncoder.encode(password))
        return userService.create(AppUser(firstName, lastName, credential).apply { this.roles.addAll(roles) })
    }

}