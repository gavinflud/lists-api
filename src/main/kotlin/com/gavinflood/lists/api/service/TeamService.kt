package com.gavinflood.lists.api.service

import com.gavinflood.lists.api.domain.AppUser
import com.gavinflood.lists.api.domain.Team
import com.gavinflood.lists.api.exception.NoMatchFoundException
import com.gavinflood.lists.api.exception.NotAuthorizedException
import com.gavinflood.lists.api.repository.TeamRepository
import com.gavinflood.lists.api.security.UserSecurity
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service

/**
 * Business logic for teams.
 */
@Service
class TeamService(

    private val teamRepository: TeamRepository,
    private val appUserService: AppUserService,
    private val userSecurity: UserSecurity

) {

    private val logger = LoggerFactory.getLogger(TeamService::class.java)

    /**
     * Create a new [Team] and add the current authenticated user as its only initial member.
     */
    fun create(team: Team): Team {
        val users = mutableSetOf(appUserService.getCurrentAuthenticatedUser())
        return create(team, users)
    }

    /**
     * Create a new [Team] and add multiple users as members.
     */
    protected fun create(team: Team, users: Collection<AppUser>): Team {
        team.members.addAll(users)
        return teamRepository.save(team)
    }

    /**
     * Find a [Team] by its unique [id].
     */
    fun findById(id: Long): Team {
        val team = teamRepository.findById(id)

        if (team.isEmpty) {
            logger.warn("No team was found with ID '$id'")
            throw NoMatchFoundException("No team was found with ID '$id'")
        }

        checkCurrentUserIsAuthorizedToAccessTeam(team.get())
        return team.get()
    }

    /**
     * Find the teams a user (identified by [userId]) is a member of.
     */
    @PreAuthorize("@userSecurity.isAdminOrSameUser(authentication, #userId)")
    fun findTeamsForUser(userId: Long, pageable: Pageable): Page<Team> {
        try {
            val user = appUserService.findById(userId)
            return teamRepository.findAllByMembersContainsAndRetiredIsFalse(user, pageable)
        } catch (exception: NoMatchFoundException) {
            logger.warn("Cannot find teams as no user exists with the ID '$userId'")
            throw exception
        }
    }

    /**
     * Update a [Team].
     */
    fun update(id: Long, updatedTeam: Team): Team {
        try {
            val team = findById(id)
            checkCurrentUserIsAuthorizedToAccessTeam(team)
            team.name = updatedTeam.name
            logger.info("Updating team ${team.id}")
            return teamRepository.save(team)
        } catch (exception: NoMatchFoundException) {
            logger.warn("Cannot update a team as none exists with the ID '$id'")
            throw exception
        }
    }

    /**
     * Retire a [Team] identified by its [id].
     */
    fun retire(id: Long) {
        try {
            val team = findById(id)
            checkCurrentUserIsAuthorizedToAccessTeam(team)
            team.retire()
            logger.info("Retiring team ${team.id}")
            teamRepository.save(team)
        } catch (exception: NoMatchFoundException) {
            logger.warn("Cannot retire a team as none exists with the ID '$id'")
            throw exception
        }
    }

    /**
     * Adds [users] to a team identified by [id].
     *
     * TODO: Add a team admin permission required to manage membership
     */
    fun addMembers(id: Long, users: Collection<AppUser>): Collection<AppUser> {
        try {
            val team = findById(id)
            checkCurrentUserIsAuthorizedToAccessTeam(team)
            team.members.addAll(users)
            logger.info("Adding users [${users.map { it.id }.joinToString()}] to team '$id'")
            return teamRepository.save(team).members
        } catch (exception: NoMatchFoundException) {
            logger.warn("Cannot add members to a team as none exists with the ID '$id'")
            throw exception
        }
    }

    /**
     * Removes [users] from a team identified by [id].
     *
     * TODO: Ensure one member remains at all times
     */
    fun removeMembers(id: Long, users: Collection<AppUser>): Collection<AppUser> {
        try {
            val team = findById(id)
            checkCurrentUserIsAuthorizedToAccessTeam(team)
            team.members.removeAll(users)
            logger.info("Removing users [${users.map { it.id }.joinToString()}] from team '$id'")
            return teamRepository.save(team).members
        } catch (exception: NoMatchFoundException) {
            logger.warn("Cannot remove members from a team as none exists with the ID '$id'")
            throw exception
        }
    }

    /**
     * Validate that the current authenticated user is either a member of the [team] or is an administrator.
     */
    private fun checkCurrentUserIsAuthorizedToAccessTeam(team: Team) {
        val currentUser = appUserService.getCurrentAuthenticatedUser()

        if (!team.members.contains(currentUser) && !userSecurity.isAdmin(currentUser)) {
            logger.warn("User '${currentUser.id}' is not a member of team '${team.id}' and cannot modify it")
            throw NotAuthorizedException("User '${currentUser.id}' is not a member of team '${team.id}'")
        }
    }

}