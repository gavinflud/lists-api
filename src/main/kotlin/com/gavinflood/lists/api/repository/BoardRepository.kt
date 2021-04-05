package com.gavinflood.lists.api.repository

import com.gavinflood.lists.api.domain.Board
import com.gavinflood.lists.api.domain.Team
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Repository for the boards table.
 */
@Repository
interface BoardRepository : JpaRepository<Board, Long> {

    /**
     * Find all boards that are owned by a team in [teams].
     */
    fun findAllByTeamIn(teams: Collection<Team>, pageable: Pageable): Page<Board>

}