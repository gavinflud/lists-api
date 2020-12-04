package com.gavinflood.lists.api.auth

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.PropertySource
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import java.util.*

@Component
@PropertySource("classpath:jwt.properties")
class JwtUtil {

    @Value("\${jwt.secret}")
    private var secret = ""

    @Value("\${jwt.timeout}")
    private var timeout = 3600000

    fun getAllClaimsFromToken(token: String): Claims {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).body
    }

    fun getUsernameFromToken(token: String): String {
        return getAllClaimsFromToken(token).subject
    }

    fun getExpirationDateFromToken(token: String): Date {
        return getAllClaimsFromToken(token).expiration
    }

    /**
     * TODO: Possibly remove as exception is thrown when expired anyway
     */
    fun isTokenExpired(token: String): Boolean {
        return getExpirationDateFromToken(token).before(Date())
    }

    fun isTokenValid(token: String, userDetails: UserDetails): Boolean {
        return getUsernameFromToken(token) == userDetails.username && !isTokenExpired(token)
    }

    fun generateToken(userDetails: UserDetails): String {
        return generateToken(mutableMapOf(), userDetails.username)
    }

    private fun generateToken(claims: Map<String, Any>, subject: String): String {
        val expiration = Calendar.getInstance()
        expiration.add(Calendar.MILLISECOND, timeout)
        return Jwts.builder()
            .setClaims(claims)
            .setSubject(subject)
            .setIssuedAt(Date())
            .setExpiration(expiration.time)
            .signWith(SignatureAlgorithm.HS512, secret)
            .compact()
    }

}