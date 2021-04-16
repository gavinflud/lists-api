package com.gavinflood.lists.api.controller

import com.gavinflood.lists.api.controller.dto.ApiResponse
import com.gavinflood.lists.api.controller.dto.BasicDTO
import org.springframework.http.ResponseEntity

/**
 * Responsible for some common response creation functionality.
 */
class Responses {

    companion object {

        /**
         * Utility function to avoid repetitive uses of [ResponseEntity.ok] passing an [ApiResponse].
         */
        fun ok(body: BasicDTO): ResponseEntity<ApiResponse> {
            return ResponseEntity.ok(ApiResponse(body))
        }

        /**
         * Utility function to avoid repetitive uses of [ResponseEntity.ok] passing an [ApiResponse].
         */
        fun ok(body: Iterable<out BasicDTO>): ResponseEntity<ApiResponse> {
            return ResponseEntity.ok(ApiResponse(body))
        }

    }

}