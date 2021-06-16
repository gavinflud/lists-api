package com.gavinflood.lists.api.controller.card

import com.gavinflood.lists.api.controller.dto.BasicDTO
import com.gavinflood.lists.api.domain.Card
import com.gavinflood.lists.api.domain.List
import java.util.*

/**
 * DTO for card data sent in requests.
 */
data class CardRequestDTO(
    val title: String,
    val description: String,
    val dueDate: Date,
    val priority: Int,
    val listId: Long
) : BasicDTO {

    /**
     * Create [Card] from [CardRequestDTO].
     */
    fun toEntity(list: List): Card {
        return Card(title, description, dueDate, priority, list)
    }

}

/**
 * DTO for card data sent in responses.
 */
data class CardResponseDTO(
    val id: Long,
    val title: String,
    val description: String,
    val dueDate: Date,
    val priority: Int,
    val listId: Long
) : BasicDTO

/**
 * Create [CardResponseDTO] from [List].
 */
fun Card.toResponseDTO(): CardResponseDTO {
    return CardResponseDTO(id, title, description, dueDate, priority, list.id)
}

/**
 * DTO for an updated card as part of a multi-update request.
 */
data class UpdateCardRequestDTO(
    val id: Long,
    val title: String,
    val description: String,
    val dueDate: Date,
    val priority: Int,
    val listId: Long
) : BasicDTO {

    /**
     * Create [Card] from [UpdateCardRequestDTO].
     */
    fun toEntity(list: List): Card {
        return Card(title, description, dueDate, priority, list)
    }

}

/**
 * DTO for updating multiple lists in one request.
 */
data class UpdateMultipleCardRequestDTO(
    val cards: Set<UpdateCardRequestDTO>
) : BasicDTO