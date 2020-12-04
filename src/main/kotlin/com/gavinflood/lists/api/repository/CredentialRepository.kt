package com.gavinflood.lists.api.repository

import com.gavinflood.lists.api.domain.Credential
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

/**
 * Repository for the credentials table.
 */
@Repository
interface CredentialRepository : JpaRepository<Credential, Long> {

    /**
     * Find a set of credentials by the email address.
     *
     * @param emailAddress the user's email address
     * @return An [Optional] that will contain the credential if a match was found
     */
    fun findDistinctByEmailAddress(emailAddress: String): Optional<Credential>

}