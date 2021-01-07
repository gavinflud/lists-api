package com.gavinflood.lists.api.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
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

    @ManyToMany
    @JoinTable(
        name = "user_roles",
        joinColumns = [JoinColumn(name = "user_id")],
        inverseJoinColumns = [JoinColumn(name = "role_id")]
    )
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    val roles: MutableSet<Role> = mutableSetOf(),

    @ManyToMany(mappedBy = "members")
    @JsonIgnore
    val teams: MutableSet<Team> = mutableSetOf(),

    // TODO: Change this to true once activation logic is complete
    @Column(name = "is_locked")
    var isLocked: Boolean = false,

    ) : BaseEntity(), UserDetails {

    /**
     * @return the permissions granted to the user
     */
    override fun getAuthorities(): Collection<GrantedAuthority> {
        return roles.flatMap { it.permissions }.map { SimpleGrantedAuthority(it.code) }
    }

    /**
     * @return the user's password
     */
    override fun getPassword(): String {
        return credential.password
    }

    /**
     * @return the user's email address
     */
    override fun getUsername(): String {
        return credential.emailAddress
    }

    /**
     * TODO: Add expiration date to accounts
     *
     * @return true if the user's account is active
     */
    override fun isAccountNonExpired(): Boolean {
        return true
    }

    /**
     * @return true if the user's account is not locked
     */
    override fun isAccountNonLocked(): Boolean {
        return !isLocked
    }

    /**
     * TODO: Add ability to expire user's credentials
     *
     * @return true if the user's credentials have not expired
     */
    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    /**
     * TODO: Add ability to disable accounts
     *
     * @return true if the user's account is enabled
     */
    override fun isEnabled(): Boolean {
        return true
    }

}