package com.gavinflood.lists.api.controller.mapper

import com.gavinflood.lists.api.controller.dto.BasicDTO
import com.gavinflood.lists.api.domain.BaseEntity

/**
 * A basic interface that all mappers mapping from a [BaseEntity] to a [BasicDTO] can extend to get common
 * functionality.
 */
interface BasicMapper<T : BaseEntity, U : BasicDTO> {

    /**
     * Map the [dto] to an instance of [T].
     */
    fun dtoToEntity(dto: U): T

    /**
     * Map the [entity] to an instance of [U].
     */
    fun entityToDTO(entity: T): U

}