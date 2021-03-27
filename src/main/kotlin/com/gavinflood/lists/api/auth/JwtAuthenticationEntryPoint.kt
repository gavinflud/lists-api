package com.gavinflood.lists.api.auth

import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Entry point used for JWT authentication.
 */
@Component
class JwtAuthenticationEntryPoint : AuthenticationEntryPoint {

    /**
     * Handles exceptions and throw an unauthorized exception whenever a JWT token is not validated.
     */
    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authException: AuthenticationException
    ) {
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized")
    }

}