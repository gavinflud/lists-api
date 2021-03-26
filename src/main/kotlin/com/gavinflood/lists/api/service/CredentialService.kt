package com.gavinflood.lists.api.service

import com.gavinflood.lists.api.domain.Credential
import com.gavinflood.lists.api.exception.UsernameAlreadyExistsException
import com.gavinflood.lists.api.repository.CredentialRepository
import org.slf4j.LoggerFactory
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

/**
 * Business logic for credentials.
 */
@Service
class CredentialService(

    private val credentialRepository: CredentialRepository,
    private val passwordEncoder: PasswordEncoder

) {

    private val logger = LoggerFactory.getLogger(CredentialService::class.java)

    /**
     * Create a new [Credential].
     */
    fun create(credential: Credential): Credential {
        val existingCredential = credentialRepository.findDistinctByEmailAddress(credential.emailAddress)
        if (existingCredential.isPresent) {
            logger.info("Cannot create a user with a username currently used by user '${existingCredential.get().id}'")
            throw UsernameAlreadyExistsException()
        }

        credential.password = passwordEncoder.encode(credential.password)
        return credentialRepository.save(credential)
    }

    /**
     * Find a single [Credential] identified by its unique emailAddress value.
     */
    fun findOne(emailAddress: String): Credential {
        val credential = credentialRepository.findDistinctByEmailAddress(emailAddress)

        return if (credential.isPresent) {
            credential.get()
        } else throw UsernameNotFoundException("No user found with username '$emailAddress'")
    }

    /**
     * Update the password for an existing set of credentials and return the saved [Credential].
     */
    fun update(updatedCredential: Credential): Credential {
        val existingCredential = findOne(updatedCredential.emailAddress)
        existingCredential.password = passwordEncoder.encode(updatedCredential.password)
        return credentialRepository.save(existingCredential)
    }

    /**
     * Retire a [Credential] identified by its email address.
     */
    fun retire(emailAddress: String) {
        val existingCredential = findOne(emailAddress)
        existingCredential.retire()
        credentialRepository.save(existingCredential)
    }

}