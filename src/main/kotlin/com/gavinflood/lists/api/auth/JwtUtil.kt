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
     * Get all claims from a token.
     *
     * @param token the encoded token
     * @return the claims map
     */
    fun getAllClaimsFromToken(token: String): Claims {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).body
    }

    /**
     * Get a username from a token.
     *
     * @param token the encoded token
     * @return the username
     */
    fun getUsernameFromToken(token: String): String {
        return getAllClaimsFromToken(token).subject
    }

    /**
     * Get the expiration date of a token.
     *
     * @param token the encoded token
     * @return the expiration date of the token
     */
    fun getExpirationDateFromToken(token: String): Date {
        return getAllClaimsFromToken(token).expiration
    }

    /**
     * Check if a token has expired.
     *
     * TODO: Possibly remove as exception is thrown when getting claims if token is expired anyway
     *
     * @param token the encoded token
     * @return true if the token has expired and false otherwise
     */
    fun isTokenExpired(token: String): Boolean {
        return getExpirationDateFromToken(token).before(Date())
    }

    /**
     * Check if a token is valid based on the username.
     *
     * @param token the encoded token
     * @param userDetails the user to check the username on the token against
     * @return true if the token is valid and false otherwise
     */
    fun isTokenValid(token: String, userDetails: UserDetails): Boolean {
        return getUsernameFromToken(token) == userDetails.username && !isTokenExpired(token)
    }

    /**
     * Generate an access token for a given user.
     *
     * @param userDetails the user the access token is for
     * @return the encoded token
     */
    fun generateAccessToken(userDetails: UserDetails): String {
        val claims = mutableMapOf<String, Any>()

        if (userDetails is AppUser) {
            claims["firstName"] = userDetails.firstName
            claims["lastName"] = userDetails.lastName
        }

        return generateToken(claims, userDetails.username, false)
    }

    /**
     * Generate a refresh token for a given user.
     *
     * @param userDetails the user the refresh token is for
     * @return the encoded token
     */
    fun generateRefreshToken(userDetails: UserDetails): String {
        return generateToken(mutableMapOf(), userDetails.username, true)
    }

    /**
     * Generic token generation function for both types of JWTs.
     *
     * @param claims the claims to be added to the token
     * @param subject the identifier for the user the token is for
     * @param isRefreshToken true if the token is a refresh token and false if it is an access token
     * @return the encoded token
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