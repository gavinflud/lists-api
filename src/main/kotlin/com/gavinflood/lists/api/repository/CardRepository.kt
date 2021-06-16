package com.gavinflood.lists.api.repository

import com.gavinflood.lists.api.domain.Card
import com.gavinflood.lists.api.domain.List
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Repository for the cards table.
 */
@Repository
interface CardRepository : JpaRepository<Card, Long> {

    /**
     * Find all cards that are owned by [list].
     */
    fun findAllByListEqualsAndRetiredIsFalseOrderByPriorityAsc(list: List): Collection<Card>

}