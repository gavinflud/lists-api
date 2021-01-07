package com.gavinflood.lists.api.domain

import javax.persistence.*

/**
 * A list is part of a board and holds a set of cards.
 *
 * For example, a list could be used to track the user stories for an upcoming product release that are "In Progress".
 */
@Entity(name = "lists")
class List(

    @Column(name = "name")
    var name: String,

    @Column(name = "priority")
    var priority: Int,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    var board: Board,

    @OneToMany(mappedBy = "list")
    val cards: MutableSet<Card> = mutableSetOf()

) : BaseEntity()