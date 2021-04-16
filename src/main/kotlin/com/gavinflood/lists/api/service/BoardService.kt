package com.gavinflood.lists.api.service

import com.gavinflood.lists.api.domain.Board
import com.gavinflood.lists.api.exception.NoMatchFoundException
import com.gavinflood.lists.api.exception.NotAuthorizedException
import com.gavinflood.lists.api.repository.BoardRepository
import com.gavinflood.lists.api.security.UserSecurity
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service

/**
 * Business logic for boards.
 */
@Service
class BoardService(

    private val boardRepository: BoardRepository,
    private val appUserService: AppUserService,
    private val teamService: TeamService,
    private val userSecurity: UserSecurity

) {

    private val logger = LoggerFactory.getLogger(TeamService::class.java)

    /**
     * Create a new [Board].
     */
    fun create(board: Board): Board {
        return boardRepository.save(board)
    }

    /**
     * Find a [Board] by its unique [id].
     */
    fun findById(id: Long): Board {
        val board = boardRepository.findById(id)

        if (board.isEmpty) {
            logger.warn("No board was found with ID '$id'")
            throw NoMatchFoundException("No board was found with ID '$id'")
        }

        checkCurrentUserIsAuthorizedToAccessBoard(board.get())
        return board.get()
    }

    /**
     * Find the boards a user has access to through their teams.
     */
    @PreAuthorize("@userSecurity.isAdminOrSameUser(authentication, #userId)")
    fun findBoardsForUser(userId: Long, pageable: Pageable): Page<Board> {
        val teams = teamService.findTeamsForUser(userId, Pageable.unpaged())
        return boardRepository.findAllByTeamInAndRetiredIsFalse(teams.content, pageable)
    }

    /**
     * Update a [Board] identified by [id].
     */
    fun update(id: Long, updatedBoard: Board): Board {
        try {
            val board = findById(id)
            checkCurrentUserIsAuthorizedToAccessBoard(board)
            board.name = updatedBoard.name
            board.description = updatedBoard.description

            if (updatedBoard.team != board.team) {
                checkCurrentUserIsAuthorizedToAccessBoard(updatedBoard)
                board.team = updatedBoard.team
            }

            logger.info("Updating board ${board.id}")
            return boardRepository.save(board)
        } catch (exception: NoMatchFoundException) {
            logger.warn("Cannot update a board as none exists with the ID '$id'")
            throw exception
        }
    }

    /**
     * Retire a board identified by [id].
     */
    fun retire(id: Long) {
        try {
            val board = findById(id)
            checkCurrentUserIsAuthorizedToAccessBoard(board)
            board.retire()
            logger.info("Retiring board ${board.id}")
            boardRepository.save(board)
        } catch (exception: NoMatchFoundException) {
            logger.warn("Cannot retire a board as none exists with the ID '$id'")
            throw exception
        }
    }

    /**
     * Validate that the current authenticated user is either a member of the team that owns [board] or is an
     * administrator.
     */
    fun checkCurrentUserIsAuthorizedToAccessBoard(board: Board) {
        val currentUser = appUserService.getCurrentAuthenticatedUser()

        if (!board.team.members.contains(currentUser) && !userSecurity.isAdmin(currentUser)) {
            logger.warn(
                "User '${currentUser.id}' is not authorized for board '${board.id}' under team " +
                        "'${board.team.id}' and cannot modify it"
            )
            throw NotAuthorizedException(
                "User '${currentUser.id}' is not authorized for board '${board.id}' under " +
                        "team '${board.team.id}'"
            )
        }
    }

}