package com.gavinflood.lists.api.controller

import com.gavinflood.lists.api.controller.dto.ApiResponse
import com.gavinflood.lists.api.exception.BadOperationException
import com.gavinflood.lists.api.exception.NoMatchFoundException
import com.gavinflood.lists.api.exception.NotAuthorizedException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

/**
 * Defines how certain exceptions are handled when responding to the client.
 */
@ControllerAdvice
class GlobalControllerExceptionHandler : ResponseEntityExceptionHandler() {

    /**
     * Handle when no match was found for a requested resource.
     */
    @ExceptionHandler(NoMatchFoundException::class)
    fun handleNoMatchFound(exception: NoMatchFoundException): ResponseEntity<ApiResponse> {
        val message = exception.message ?: "No match could be found with that ID"
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse(ApiResponse.ERROR_NOT_FOUND, message))
    }

    /**
     * Handle when a user is not authorized to perform an operation.
     */
    @ExceptionHandler(NotAuthorizedException::class)
    fun handleNotAuthorized(exception: NotAuthorizedException): ResponseEntity<ApiResponse> {
        val message = "You are not authorized to perform that operation"
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse(ApiResponse.ERROR_UNAUTHORIZED, message))
    }

    /**
     * Handle when a requested operation is invalid.
     */
    @ExceptionHandler(BadOperationException::class)
    fun handleBadOperation(exception: BadOperationException): ResponseEntity<ApiResponse> {
        val message = exception.message ?: "That operation is invalid and could not be completed"
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse(ApiResponse.ERROR_BAD_OPERATION, message))
    }

}