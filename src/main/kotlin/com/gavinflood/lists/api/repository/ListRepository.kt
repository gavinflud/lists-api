package com.gavinflood.lists.api.repository

import com.gavinflood.lists.api.domain.Board
import com.gavinflood.lists.api.domain.List
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Repository for the lists table.
 */
@Repository
interface ListRepository : JpaRepository<List, Long> {

    /**
     * Find all lists that are owned by [board].
     */
    fun findAllByBoardEqualsAndRetiredIsFalseOrderByPriorityAsc(board: Board): Collection<List>

}