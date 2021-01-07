package com.gavinflood.lists.api.repository

import com.gavinflood.lists.api.domain.Permission
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

/**
 * Repository for the permissions table.
 */
@Repository
interface PermissionRepository : JpaRepository<Permission, Long> {

    /**
     * Find a distinct permission by its code.
     *
     * @param code identifies the permission
     * @return an optional container for the matching permission if it exists
     */
    fun findDistinctByCodeAndRetiredIsFalse(code: String): Optional<Permission>

}