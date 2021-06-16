package com.gavinflood.lists.api.controller.card

import com.gavinflood.lists.api.controller.Responses
import com.gavinflood.lists.api.controller.dto.ApiResponse
import com.gavinflood.lists.api.domain.Card
import com.gavinflood.lists.api.service.CardService
import com.gavinflood.lists.api.service.ListService
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * Controller with REST endpoints for list management.
 */
@RestController
@RequestMapping("/api/cards")
class CardController(

    private val cardService: CardService,
    private val listService: ListService

) {

    /**
     * Create a [Card].
     */
    @PostMapping
    fun create(@RequestBody dto: CardRequestDTO): ResponseEntity<ApiResponse> {
        val list = listService.findById(dto.listId)
        val card = cardService.create(dto.toEntity(list))
        return Responses.ok(card.toResponseDTO())
    }

    /**
     * Get the cards under a particular list (identified by [listId]). Results are paginated and in the form of
     * [CardResponseDTO] wrapped in an [ApiResponse].
     */
    @GetMapping
    fun getMultiple(@RequestParam listId: Long, pageable: Pageable): ResponseEntity<ApiResponse> {
        val list = listService.findById(listId)
        val cards = cardService.findAllUnderList(list)
        return Responses.ok(cards.map { it.toResponseDTO() })
    }

    /**
     * Find a card identified by its [id].
     */
    @GetMapping("/{id}")
    fun getOne(@PathVariable id: Long): ResponseEntity<ApiResponse> {
        val card = cardService.findById(id)
        return Responses.ok(card.toResponseDTO())
    }

    /**
     * Update a card identified by its [id]. The response should be a [CardResponseDTO] wrapped in an [ApiResponse].
     */
    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody updatedCardDTO: CardRequestDTO): ResponseEntity<ApiResponse> {
        val list = listService.findById(updatedCardDTO.listId)
        val updatedCard = cardService.update(id, updatedCardDTO.toEntity(list))
        return Responses.ok(updatedCard.toResponseDTO())
    }

    /**
     * Update multiple cards at once.
     */
    @PutMapping
    fun updateMultiple(@RequestBody updateCardsDTO: UpdateMultipleCardRequestDTO): ResponseEntity<ApiResponse> {
        val updatedCardsById = updateCardsDTO.cards.associate { cardDTO ->
            cardDTO.id to cardDTO.toEntity(listService.findById(cardDTO.listId))
        }
        val updatedCards = cardService.updateMultiple(updatedCardsById)
        return Responses.ok(updatedCards.map { it.toResponseDTO() })
    }

    /**
     * Retire a card identified by its [id].
     */
    @DeleteMapping("/{id}")
    fun retire(@PathVariable id: Long): ResponseEntity<ApiResponse> {
        return ResponseEntity.ok(ApiResponse(cardService.retire(id)))
    }

}