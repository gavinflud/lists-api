package com.gavinflood.lists.api.repository

import com.gavinflood.lists.api.domain.Permission
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

/**
 * Repository for the permissions table.
 */
@Repository
interface PermissionRepository : JpaRepository<Permission, Long> {

    /**
     * Find a distinct [Permission] by its [code].
     */
    fun findDistinctByCodeAndRetiredIsFalse(code: String): Optional<Permission>

    /**
     * Find all permissions.
     */
    fun findAllByRetiredIsFalse(pageable: Pageable): Page<Permission>

}