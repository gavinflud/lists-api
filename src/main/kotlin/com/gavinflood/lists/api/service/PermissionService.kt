package com.gavinflood.lists.api.service

import com.gavinflood.lists.api.domain.Permission
import com.gavinflood.lists.api.exception.AlreadyExistsException
import com.gavinflood.lists.api.exception.NoMatchFoundException
import com.gavinflood.lists.api.repository.PermissionRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

/**
 * Business logic for permissions.
 */
@Service
class PermissionService(private val permissionRepository: PermissionRepository) {

    private val logger: Logger = LoggerFactory.getLogger(PermissionService::class.java)

    /**
     * Find a distinct [Permission] by its [code].
     */
    fun findByCode(code: String): Permission {
        return permissionRepository.findDistinctByCodeAndRetiredIsFalse(code).orElseThrow {
            logger.warn("Could not find permission with code '$code'")
            NoMatchFoundException("Could not find permission with code '$code'")
        }
    }

    /**
     * Find all permissions.
     */
    fun findAll(pageable: Pageable): Page<Permission> {
        return permissionRepository.findAllByRetiredIsFalse(pageable)
    }

    /**
     * Create a new [Permission].
     */
    fun create(permission: Permission): Permission {
        if (permissionRepository.findDistinctByCodeAndRetiredIsFalse(permission.code).isEmpty) {
            return permissionRepository.save(permission)
        }

        throw AlreadyExistsException("Can't create permission with code '${permission.code}' as it already exists")
    }

    /**
     * Update an existing [Permission] identified by its [code].
     */
    fun update(code: String, updatedPermission: Permission): Permission {
        val existingPermission = findByCode(code)
        val updatedPermissionCode = updatedPermission.code

        if (code != updatedPermissionCode
            && permissionRepository.findDistinctByCodeAndRetiredIsFalse(updatedPermissionCode).isPresent
        ) {
            throw AlreadyExistsException("Can't update permission with code '$updatedPermissionCode' as it already exists")
        }

        existingPermission.code = updatedPermission.code
        existingPermission.description = updatedPermission.description
        return permissionRepository.save(existingPermission)
    }

    /**
     * Retire an existing [Permission] identified by its [code].
     */
    fun retire(code: String) {
        try {
            val permission = findByCode(code)
            permission.retire()
            logger.info("Retiring permission ${permission.id}")
            permissionRepository.save(permission)
        } catch (exception: NoMatchFoundException) {
            logger.warn("Cannot retire a permission as none exists with the code '$code'")
            throw exception
        }
    }

}