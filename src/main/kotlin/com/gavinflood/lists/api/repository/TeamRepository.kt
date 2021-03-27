package com.gavinflood.lists.api.repository

import com.gavinflood.lists.api.domain.AppUser
import com.gavinflood.lists.api.domain.Team
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Repository for the teams table.
 */
@Repository
interface TeamRepository : JpaRepository<Team, Long> {

    /**
     * Find all teams that contain the [user].
     */
    fun findAllByMembersContains(user: AppUser, pageable: Pageable): Page<Team>

}