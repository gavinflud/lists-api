package com.gavinflood.lists.api.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

/**
 * Thrown when a user tries to set their username to one that is already in use.
 */
class UsernameAlreadyExistsException : Exception()

/**
 * Thrown when no match was found for a given query.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
class NoMatchFoundException(message: String) : Exception(message)

/**
 * Thrown when an attempt was made to create something that already exists.
 */
class AlreadyExistsException(message: String) : Exception(message)

/**
 * Thrown when the current user is not authorized to carry out a specific action.
 */
@ResponseStatus(HttpStatus.UNAUTHORIZED)
class NotAuthorizedException(message: String) : Exception(message)