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
     * Find all teams that contain a given user.
     *
     * @param user that should be a member of the team
     * @return a page of the teams the user is a member of
     */
    fun findAllByMembersContains(user: AppUser, pageable: Pageable): Page<Team>

}