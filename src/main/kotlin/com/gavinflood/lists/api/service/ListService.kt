package com.gavinflood.lists.api.service

import com.gavinflood.lists.api.domain.Board
import com.gavinflood.lists.api.domain.List
import com.gavinflood.lists.api.exception.NoMatchFoundException
import com.gavinflood.lists.api.exception.NotAuthorizedException
import com.gavinflood.lists.api.repository.ListRepository
import com.gavinflood.lists.api.security.UserSecurity
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

/**
 * Business logic for lists.
 */
@Service
class ListService(

    private val listRepository: ListRepository,
    private val appUserService: AppUserService,
    private val boardService: BoardService,
    private val userSecurity: UserSecurity

) {

    private val logger = LoggerFactory.getLogger(ListService::class.java)

    /**
     * Create a new [List].
     */
    fun create(list: List): List {
        return listRepository.save(list)
    }

    /**
     * Find a [List] by its unique [id].
     */
    fun findById(id: Long): List {
        val list = listRepository.findById(id)

        if (list.isEmpty) {
            logger.warn("No list was found with ID '$id'")
            throw NoMatchFoundException("No list was found with ID '$id'")
        }

        checkCurrentUserIsAuthorizedToAccessList(list.get())
        return list.get()
    }

    /**
     * Find all lists under [board].
     */
    fun findAllUnderBoard(board: Board): Collection<List> {
        boardService.checkCurrentUserIsAuthorizedToAccessBoard(board)
        return listRepository.findAllByBoardEqualsAndRetiredIsFalseOrderByPriorityAsc(board)
    }

    /**
     * Update a [List] identified by [id].
     */
    fun update(id: Long, updatedList: List): List {
        try {
            val list = findById(id)
            checkCurrentUserIsAuthorizedToAccessList(list)
            list.name = updatedList.name
            list.priority = updatedList.priority

            if (updatedList.board != list.board) {
                checkCurrentUserIsAuthorizedToAccessList(updatedList)
                list.board = updatedList.board
            }

            logger.info("Updating list ${list.id}")
            return listRepository.save(list)
        } catch (exception: NoMatchFoundException) {
            logger.warn("Cannot update a list as none exists with the ID '$id'")
            throw exception
        }
    }

    /**
     * Retire a list identified by [id].
     */
    fun retire(id: Long) {
        try {
            val list = findById(id)
            checkCurrentUserIsAuthorizedToAccessList(list)
            list.retire()
            logger.info("Retiring list ${list.id}")
            listRepository.save(list)
        } catch (exception: NoMatchFoundException) {
            logger.warn("Cannot retire a list as none exists with the ID '$id'")
            throw exception
        }
    }

    /**
     * Validate that the current authenticated user is either a member of the team that owns [list] or is an
     * administrator.
     */
    private fun checkCurrentUserIsAuthorizedToAccessList(list: List) {
        val currentUser = appUserService.getCurrentAuthenticatedUser()

        if (!list.board.team.members.contains(currentUser) && !userSecurity.isAdmin(currentUser)) {
            logger.warn(
                "User '${currentUser.id}' is not authorized for list '${list.id}' under board " +
                        "'${list.board.id}' and cannot modify it"
            )
            throw NotAuthorizedException(
                "User '${currentUser.id}' is not authorized for list '${list.id}' under board " +
                        "'${list.board.id}' and cannot modify it"
            )
        }
    }

}