package com.gavinflood.lists.api.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.PropertySource
import org.springframework.stereotype.Component

/**
 * Properties wrapper for CORS configuration.
 */
@Component
@PropertySource("classpath:cors.properties")
class CorsProperties {

    @Value("\${cors.allowed-origins}")
    var allowedOrigins = ""

}