package com.gavinflood.lists.api.auth

import com.gavinflood.lists.api.domain.AppUser
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.PropertySource
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import java.util.*

/**
 * Utility class backed by a properties file for JWTs and used to generate a token.
 */
@Component
@PropertySource("classpath:jwt.properties")
class JwtUtil {

    @Value("\${jwt.secret}")
    private var secret = ""

    @Value("\${jwt.access-timeout}")
    private var accessTimeout = 60000

    @Value("\${jwt.refresh-timeout}")
    private var refreshTimeout = 21600000

    /**
     * Get all [Claims] from a [token].
     */
    fun getAllClaimsFromToken(token: String): Claims {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).body
    }

    /**
     * Get a username from a [token].
     */
    fun getUsernameFromToken(token: String): String {
        return getAllClaimsFromToken(token).subject
    }

    /**
     * Get the expiration date of a [token].
     */
    fun getExpirationDateFromToken(token: String): Date {
        return getAllClaimsFromToken(token).expiration
    }

    /**
     * Check if a [token] has expired.
     *
     * TODO: Possibly remove as exception is thrown when getting claims if token is expired anyway
     */
    fun isTokenExpired(token: String): Boolean {
        return getExpirationDateFromToken(token).before(Date())
    }

    /**
     * Check if a [token] is valid based on the username.
     */
    fun isTokenValid(token: String, userDetails: UserDetails): Boolean {
        return getUsernameFromToken(token) == userDetails.username && !isTokenExpired(token)
    }

    /**
     * Generate an access token for a given [userDetails].
     */
    fun generateAccessToken(userDetails: UserDetails): String {
        val claims = mutableMapOf<String, Any>()

        if (userDetails is AppUser) {
            claims["firstName"] = userDetails.firstName
            claims["lastName"] = userDetails.lastName
            claims["id"] = userDetails.id
        }

        return generateToken(claims, userDetails.username, false)
    }

    /**
     * Generate a refresh token for a given [userDetails].
     */
    fun generateRefreshToken(userDetails: UserDetails): String {
        return generateToken(mutableMapOf(), userDetails.username, true)
    }

    /**
     * Generic token generation function for both types of JWTs.
     *
     * The [claims] and [subject] will be populated directly on the resulting JWT, while the [isRefreshToken] flag is
     * used to determine the expiration time of this token.
     */
    private fun generateToken(claims: Map<String, Any>, subject: String, isRefreshToken: Boolean): String {
        val expiration = Calendar.getInstance()
        val timeout = if (isRefreshToken) refreshTimeout else accessTimeout
        expiration.add(Calendar.MILLISECOND, timeout)
        return Jwts.builder()
            .setClaims(claims)
            .setSubject(subject)
            .setId(UUID.randomUUID().toString())
            .setIssuedAt(Date())
            .setExpiration(expiration.time)
            .signWith(SignatureAlgorithm.HS512, secret)
            .compact()
    }

}