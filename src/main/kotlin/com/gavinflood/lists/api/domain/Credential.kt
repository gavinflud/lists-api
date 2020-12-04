package com.gavinflood.lists.api.domain

import javax.persistence.Column
import javax.persistence.Entity

/**
 * The login credentials related to a specific user that are used for authentication.
 */
@Entity(name = "credentials")
class Credential(

    @Column(name = "email_address")
    var emailAddress: String,

    @Column(name = "password")
    var password: String

) : BaseEntity()