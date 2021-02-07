package com.gavinflood.lists.api.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

class UsernameAlreadyExistsException : Exception()

@ResponseStatus(HttpStatus.NOT_FOUND)
class NoMatchFoundException(message: String) : Exception(message)

class AlreadyExistsException(message: String) : Exception(message)

@ResponseStatus(HttpStatus.UNAUTHORIZED)
class NotAuthorizedException(message: String) : Exception(message)