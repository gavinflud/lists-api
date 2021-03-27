package com.gavinflood.lists.api.repository

import com.gavinflood.lists.api.domain.Role
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

/**
 * Repository for the roles table.
 */
@Repository
interface RoleRepository : JpaRepository<Role, Long> {

    /**
     * Find a distinct [Role] by its [code].
     */
    fun findDistinctByCodeAndRetiredIsFalse(code: String): Optional<Role>

    /**
     * Find all roles that have a code in [codes].
     */
    fun findAllByCodeInAndRetiredIsFalse(codes: Set<String>): Set<Role>

    /**
     * Find all roles.
     */
    fun findAllByRetiredIsFalse(pageable: Pageable): Page<Role>

}