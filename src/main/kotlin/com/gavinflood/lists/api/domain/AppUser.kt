package com.gavinflood.lists.api.domain

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import javax.persistence.*

/**
 * A user of the application that should be able to authenticate and perform various functions depending on their role.
 */
@Entity(name = "users")
class AppUser(

    @Column(name = "first_name")
    var firstName: String,

    @Column(name = "last_name")
    var lastName: String,

    @OneToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "credential_id", referencedColumnName = "id")
    var credential: Credential,

    ) : BaseEntity(), UserDetails {

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_roles",
        joinColumns = [JoinColumn(name = "user_id")],
        inverseJoinColumns = [JoinColumn(name = "role_id")]
    )
    val roles: MutableSet<Role> = mutableSetOf()

    @ManyToMany(mappedBy = "members")
    val teams: MutableSet<Team> = mutableSetOf()

    // TODO: Change this to true once activation logic is complete
    @Column(name = "is_locked")
    var isLocked: Boolean = false

    /**
     * Return the permissions granted to the user
     */
    override fun getAuthorities(): Collection<GrantedAuthority> {
        return roles.flatMap { it.permissions }.map { SimpleGrantedAuthority(it.code) }
    }

    /**
     * Return the user's password
     */
    override fun getPassword(): String {
        return credential.password
    }

    /**
     * Returns the user's email address
     */
    override fun getUsername(): String {
        return credential.emailAddress
    }

    /**
     * TODO: Add expiration date to accounts
     *
     * Returns true if the user's account is active
     */
    override fun isAccountNonExpired(): Boolean {
        return true
    }

    /**
     * Returns true if the user's account is not locked
     */
    override fun isAccountNonLocked(): Boolean {
        return !isLocked
    }

    /**
     * TODO: Add ability to expire user's credentials
     *
     * Returns true if the user's credentials have not expired
     */
    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    /**
     * Returns true if the user's account is enabled.
     */
    override fun isEnabled(): Boolean {
        return true
    }

}