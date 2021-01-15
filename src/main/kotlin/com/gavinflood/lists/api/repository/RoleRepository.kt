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
     * Find a distinct role by its code.
     *
     * @param code identifies the role
     * @return an optional container for the matching role if it exists
     */
    fun findDistinctByCodeAndRetiredIsFalse(code: String): Optional<Role>

    /**
     * Find all roles that match a set of codes.
     *
     * @param codes identifies the roles
     * @return all matching roles
     */
    fun findAllByCodeInAndRetiredIsFalse(codes: Set<String>): Set<Role>

    /**
     * Find all roles.
     *
     * @param pageable defines the page number and results per page
     * @return a page of roles
     */
    fun findAllByRetiredIsFalse(pageable: Pageable): Page<Role>

}