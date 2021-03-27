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
     * Find a [Credential] identified by the email address.
     */
    fun findDistinctByEmailAddress(emailAddress: String): Optional<Credential>

}