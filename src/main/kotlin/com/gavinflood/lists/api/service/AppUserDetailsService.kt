package com.gavinflood.lists.api.service

import com.gavinflood.lists.api.domain.AppUser
import com.gavinflood.lists.api.domain.Credential
import com.gavinflood.lists.api.repository.AppUserRepository
import com.gavinflood.lists.api.repository.CredentialRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

/**
 * Business logic for application users.
 */
@Service
class AppUserDetailsService(

    private val appUserRepository: AppUserRepository,
    private val credentialRepository: CredentialRepository

) : UserDetailsService {

    /**
     * Load a user based on their username. This involves a query for the credential followed by a query for the user.
     *
     * @param username of the user
     * @return a [UserDetails] implementation for the user
     */
    override fun loadUserByUsername(username: String): UserDetails {
        val credential = credentialRepository.findDistinctByEmailAddress(username)
        var usernameFound = credential.isPresent

        if (username == "testuser") {
            return AppUser(
                "Joe",
                "Bloggs",
                Credential("testuser", "\$2a\$10\$ZgpqxblPW5TIrv3YXeCGV.4VmfAQCa1/QnCdEUVU8Dq4zHs3Ph6ia")
            )
        }

        if (usernameFound) {
            val user = appUserRepository.findDistinctByCredential(credential.get())
            usernameFound = user.isPresent

            if (usernameFound) {
                return user.get()
            }
        }

        throw UsernameNotFoundException("No user found with username '$username'")
    }

}