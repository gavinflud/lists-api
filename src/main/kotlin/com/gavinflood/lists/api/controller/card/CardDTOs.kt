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
    val priority: Int,
    val listId: Long
) : BasicDTO {

    /**
     * Create [Card] from [CardRequestDTO].
     */
    fun toEntity(list: List): Card {
        return Card(title, priority, list)
    }

}

/**
 * DTO for card data sent in responses.
 */
data class CardResponseDTO(
    val id: Long,
    val title: String,
    val priority: Int,
    val listId: Long,
    val description: String?,
    val dueDate: Date?
) : BasicDTO

/**
 * Create [CardResponseDTO] from [List].
 */
fun Card.toResponseDTO(): CardResponseDTO {
    return CardResponseDTO(id, title, priority, list.id, description, dueDate)
}

/**
 * DTO for an updated card as part of a multi-update request.
 */
data class UpdateCardRequestDTO(
    val id: Long,
    val title: String,
    val priority: Int,
    val listId: Long,
    val description: String?,
    val dueDate: Date?
) : BasicDTO {

    /**
     * Create [Card] from [UpdateCardRequestDTO].
     */
    fun toEntity(list: List): Card {
        val description = description
        val dueDate = dueDate
        return Card(title, priority, list).apply {
            this.description = description
            this.dueDate = dueDate
        }
    }

}

/**
 * DTO for updating multiple lists in one request.
 */
data class UpdateMultipleCardRequestDTO(
    val cards: Set<UpdateCardRequestDTO>
) : BasicDTO