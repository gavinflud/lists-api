package com.gavinflood.lists.api.auth

import com.gavinflood.lists.api.service.AppUserService
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.SignatureException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Filter to handle JWT validation before allowing a request through to the API.
 */
@Component
class JwtRequestFilter(

    private val appUserDetailsService: AppUserService,
    private val jwtUtil: JwtUtil

) : OncePerRequestFilter() {

    private val authorizationHeaderName = "authorization"
    private val authorizationHeaderPrefix = "Bearer "

    /**
     * Perform JWT validation.
     *
     * @param request made from the client
     * @param response that will be sent to the client
     * @param filterChain used to invoke the next filter or end the chain
     */
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val requestTokenHeader = request.getHeader(authorizationHeaderName)

        if (requestTokenHeader != null && requestTokenHeader.startsWith(authorizationHeaderPrefix)) {
            val token = requestTokenHeader.substring(authorizationHeaderPrefix.length)
            try {
                val username = jwtUtil.getUsernameFromToken(token)

                if (SecurityContextHolder.getContext().authentication == null) {
                    val userDetails = appUserDetailsService.loadUserByUsername(username)

                    if (jwtUtil.isTokenValid(token, userDetails)) {
                        val authToken = UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities)
                        authToken.details = WebAuthenticationDetailsSource().buildDetails(request)
                        SecurityContextHolder.getContext().authentication = authToken
                    }
                }
            } catch (exception: ExpiredJwtException) {
                logger.info("The following JWT has expired: '$token'")
            } catch (exception: SignatureException) {
                logger.warn("The following JWT is not valid and should not be trusted: '$token'")
            }
        }

        filterChain.doFilter(request, response)
    }

}