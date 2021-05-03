package com.gavinflood.lists.api.domain

import javax.persistence.*

/**
 * A role that can be assigned to a user to provide them with a set of application permissions.
 */
@Entity(name = "roles")
class Role(

    @Column(name = "code", unique = true)
    var code: String,

    @Column(name = "description")
    var description: String,

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "role_permissions",
        joinColumns = [JoinColumn(name = "role_id")],
        inverseJoinColumns = [JoinColumn(name = "permission_id")]
    )
    val permissions: MutableSet<Permission> = mutableSetOf(),

    @ManyToMany(mappedBy = "roles")
    val users: MutableSet<AppUser> = mutableSetOf()

) : BaseEntity()