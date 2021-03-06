package com.gavinflood.lists.api.domain

import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne

/**
 * A card represents a piece of work or a task to be done. It could also just be informative.
 */
@Entity(name = "cards")
class Card(

    @Column(name = "title")
    var title: String,

    @Column(name = "priority")
    var priority: Int,

    @ManyToOne
    @JoinColumn(name = "list_id")
    var list: List

) : BaseEntity() {

    @Column(name = "description")
    var description: String? = null

    @Column(name = "due_date")
    var dueDate: Date? = null

}