package com.gavinflood.lists.api.domain

import javax.persistence.*

/**
 * A team is a group of people that can have access to one or more boards.
 */
@Entity(name = "teams")
class Team(

    @Column(name = "name")
    var name: String,

    ) : BaseEntity() {

    @ManyToMany
    @JoinTable(
        name = "team_members",
        joinColumns = [JoinColumn(name = "team_id")],
        inverseJoinColumns = [JoinColumn(name = "user_id")]
    )
    val members: MutableSet<AppUser> = mutableSetOf()

    @OneToMany(mappedBy = "team")
    val boards: MutableSet<Board> = mutableSetOf()

}