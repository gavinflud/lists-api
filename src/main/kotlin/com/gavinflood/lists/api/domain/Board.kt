package com.gavinflood.lists.api.domain

import javax.persistence.*

/**
 * A board is owned by a team and contains a set of lists.
 *
 * For example, a board could be used to track the different user stories for an upcoming product release.
 */
@Entity(name = "boards")
class Board(

    @Column(name = "name")
    var name: String,

    @Column(name = "description")
    var description: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    var team: Team,

    @OneToMany(mappedBy = "board")
    var lists: MutableSet<List> = mutableSetOf()

) : BaseEntity()