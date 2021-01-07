package com.gavinflood.lists.api.exception

class UsernameAlreadyExistsException : Exception()

class NoMatchFoundException(message: String) : Exception()

class AlreadyExistsException(message: String) : Exception(message)