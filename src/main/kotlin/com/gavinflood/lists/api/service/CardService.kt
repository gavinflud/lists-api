package com.gavinflood.lists.api.service

import com.gavinflood.lists.api.domain.Card
import com.gavinflood.lists.api.domain.List
import com.gavinflood.lists.api.exception.NoMatchFoundException
import com.gavinflood.lists.api.exception.NotAuthorizedException
import com.gavinflood.lists.api.repository.CardRepository
import com.gavinflood.lists.api.security.UserSecurity
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

/**
 * Business logic for lists.
 *
 * TODO: Handle priority correction on create/update/delete
 */
@Service
class CardService(

    private val cardRepository: CardRepository,
    private val appUserService: AppUserService,
    private val listService: ListService,
    private val userSecurity: UserSecurity

) {

    private val logger = LoggerFactory.getLogger(CardService::class.java)

    /**
     * Create a new [Card].
     */
    fun create(card: Card): Card {
        return cardRepository.save(card)
    }

    /**
     * Find a [Card] by its unique [id].
     */
    fun findById(id: Long): Card {
        val card = cardRepository.findById(id)

        if (card.isEmpty) {
            logger.warn("No card was found with ID '$id'")
            throw NoMatchFoundException("No card was found with ID '$id'")
        }

        checkCurrentUserIsAuthorizedToAccessCard(card.get())
        return card.get()
    }

    /**
     * Find all cards under [list].
     *
     * TODO: Add pagination
     */
    fun findAllUnderList(list: List): Collection<Card> {
        listService.checkCurrentUserIsAuthorizedToAccessList(list)
        return cardRepository.findAllByListEqualsAndRetiredIsFalseOrderByPriorityAsc(list)
    }

    /**
     * Update a [Card] identified by [id].
     */
    fun update(id: Long, updatedCard: Card): Card {
        try {
            val card = findById(id)
            checkCurrentUserIsAuthorizedToAccessCard(card)
            updateCardInternal(card, updatedCard)

            if (updatedCard.list != card.list) {
                checkCurrentUserIsAuthorizedToAccessCard(updatedCard)
                card.list = updatedCard.list
            }

            logger.info("Updating card ${card.id}")
            return cardRepository.save(card)
        } catch (exception: NoMatchFoundException) {
            logger.warn("Cannot update a card as none exists with the ID '$id'")
            throw exception
        }
    }

    /**
     * Update multiple cards at once. Standard use-case for this would be reordering cards.
     */
    fun updateMultiple(updatedCardsById: Map<Long, Card>): Set<Card> {
        try {
            return updatedCardsById
                .mapKeys { findById(it.key) }
                .onEach { entry ->
                    val existingCard = entry.key
                    val updatedCard = entry.value

                    if (existingCard.list != updatedCard.list) {
                        listService.checkCurrentUserIsAuthorizedToAccessList(updatedCard.list)
                    }

                    checkCurrentUserIsAuthorizedToAccessCard(existingCard)
                }.map { entry ->
                    val existingCard = entry.key
                    val updatedCard = entry.value
                    updateCardInternal(existingCard, updatedCard)
                    existingCard.list = updatedCard.list

                    logger.info("Updating card ${existingCard.id}")
                    cardRepository.save(existingCard)
                }.toSet()
        } catch (exception: NoMatchFoundException) {
            logger.warn("Cannot update cards with IDs [${updatedCardsById.keys.joinToString(",")}] as one was not found")
            throw exception
        }
    }

    /**
     * Retire a card identified by [id].
     */
    fun retire(id: Long) {
        try {
            val card = findById(id)
            checkCurrentUserIsAuthorizedToAccessCard(card)
            card.retire()
            logger.info("Retiring card ${card.id}")
            cardRepository.save(card)
        } catch (exception: NoMatchFoundException) {
            logger.warn("Cannot retire a card as none exists with the ID '$id'")
            throw exception
        }
    }

    /**
     * Update an [existingCard] with the values from an [updatedCard].
     */
    private fun updateCardInternal(existingCard: Card, updatedCard: Card) {
        existingCard.title = updatedCard.title
        existingCard.description = updatedCard.description
        existingCard.dueDate = updatedCard.dueDate
        existingCard.priority = updatedCard.priority
    }

    /**
     * Validate that the current authenticated user is either a member of the team that owns [card] or is an
     * administrator.
     */
    fun checkCurrentUserIsAuthorizedToAccessCard(card: Card) {
        val currentUser = appUserService.getCurrentAuthenticatedUser()
        val team = card.list.board.team

        if (!team.members.contains(currentUser) && !userSecurity.isAdmin(currentUser)) {
            logger.warn(
                "User '${currentUser.id}' is not authorized for card '${card.id}' under list " +
                        "'${card.list.id}' and cannot modify it"
            )
            throw NotAuthorizedException(
                "User '${currentUser.id}' is not authorized for card '${card.id}' under list " +
                        "'${card.list.id}' and cannot modify it"
            )
        }
    }

}