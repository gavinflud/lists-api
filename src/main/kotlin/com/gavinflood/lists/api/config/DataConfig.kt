package com.gavinflood.lists.api.config

import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.transaction.annotation.EnableTransactionManagement

/**
 * Database and JPA configuration for the API.
 */
@Configuration
@EnableJpaRepositories("com.gavinflood.lists.api")
@EnableTransactionManagement
class DataConfig