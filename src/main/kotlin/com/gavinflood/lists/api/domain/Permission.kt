package com.gavinflood.lists.api.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.ManyToMany

/**
 * Provides a user with the permission to perform something specific in the application (for example, adding an item to
 * a list or viewing the administration reports).
 */
@Entity(name = "permissions")
class Permission(

    @Column(name = "code", unique = true)
    var code: String,

    @Column(name = "description")
    var description: String,

    @ManyToMany(mappedBy = "permissions")
    @JsonIgnore
    val roles: MutableSet<Role> = mutableSetOf()

) : BaseEntity()