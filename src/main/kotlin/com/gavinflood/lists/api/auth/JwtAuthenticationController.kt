package com.gavinflood.lists.api.auth

import com.gavinflood.lists.api.service.AppUserDetailsService
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Controller used to authenticate users and provide them with a JWT.
 *
 * @param authenticationManager used to authenticate a user's credentials
 * @param jwtUtil generates the JWT
 * @param userDetailsService retrieves the user's details for the JWT
 */
@RestController
@RequestMapping("/api/authenticate")
class JwtAuthenticationController(

    private val authenticationManager: AuthenticationManager,
    private val jwtUtil: JwtUtil,
    private val userDetailsService: AppUserDetailsService

) {

    private val logger = LoggerFactory.getLogger(JwtAuthenticationController::class.java)

    /**
     * Authenticate a set of credentials and produce a JWT.
     *
     * @param request body containing the username and password
     */
    @PostMapping
    fun authenticate(@RequestBody request: JwtAuthenticationRequest): ResponseEntity<JwtResponse> {
        return try {
            authenticationManager.authenticate(UsernamePasswordAuthenticationToken(request.username, request.password))
            val userDetails = userDetailsService.loadUserByUsername(request.username)
            val token = jwtUtil.generateToken(userDetails)
            ResponseEntity.ok(JwtResponse(token))
        } catch (exception: Exception) {
            logger.warn("'${request.username}' failed to authenticate due to '${exception.message}'", exception)
            ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }
    }

}

/**
 * Simple class to store a request with the user's credentials as the body.
 *
 * @param username the user's username
 * @param password the user's password
 */
data class JwtAuthenticationRequest(val username: String, val password: String)

/**
 * Simple class to store a response with the user's JWT as the body.
 *
 * @param token the JWT that the user can use for authentication
 */
data class JwtResponse(val token: String)