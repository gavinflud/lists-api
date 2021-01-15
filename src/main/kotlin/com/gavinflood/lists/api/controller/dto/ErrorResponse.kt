package com.gavinflood.lists.api.controller.dto

/**
 * DTO for a response from the API.
 *
 * @param response the response object (optional)
 */
class ApiResponse(val response: Any?) {

    var errorCode: String? = null
    var errorDescription: String? = null

    /**
     * @param errorCode the error that occurred (optional)
     * @param errorDescription describes the error and any steps that can be taken to rectify it (optional)
     */
    constructor(errorCode: String, errorDescription: String) : this(null) {
        this.errorCode = errorCode
        this.errorDescription = errorDescription
    }

    companion object {
        const val ERROR_NOT_FOUND = "L1000"
        const val CONFLICT = "L1001"
    }

}