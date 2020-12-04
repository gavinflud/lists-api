package com.gavinflood.lists.api.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import javax.persistence.*

/**
 * A role that can be assigned to a user to provide them with a set of application permissions.
 */
@Entity(name = "roles")
class Role(

    @Column
    var code: String,

    @Column
    var description: String,

    @ManyToMany
    @JoinTable(
        name = "role_permissions",
        joinColumns = [JoinColumn(name = "role_id")],
        inverseJoinColumns = [JoinColumn(name = "permission_id")]
    )
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    val permissions: MutableSet<Permission> = mutableSetOf(),

    @ManyToMany(mappedBy = "roles")
    @JsonIgnore
    val users: MutableSet<AppUser> = mutableSetOf()

) : BaseEntity()