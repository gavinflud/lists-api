package com.gavinflood.lists.api.data

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.PropertySource
import org.springframework.stereotype.Component

/**
 * Properties wrapper for data pre-loading.
 */
@Component
@PropertySource("classpath:preload.properties")
class PreloadProperties {

    @Value("\${preload.user.admin.username}")
    var userAdminUsername = ""

    @Value("\${preload.user.admin.firstname}")
    var userAdminFirstName = ""

    @Value("\${preload.user.admin.lastname}")
    var userAdminLastName = ""

    @Value("\${preload.user.admin.password}")
    var userAdminPassword = ""

    @Value("\${preload.permission.default.code}")
    var permissionDefaultCode = ""

    @Value("\${preload.permission.default.description}")
    var permissionDefaultDescription = ""

    @Value("\${preload.permission.admin.code}")
    var permissionAdminCode = ""

    @Value("\${preload.permission.admin.description}")
    var permissionAdminDescription = ""

    @Value("\${preload.role.user.code}")
    var roleUserCode = ""

    @Value("\${preload.role.user.description}")
    var roleUserDescription = ""

    @Value("\${preload.role.admin.code}")
    var roleAdminCode = ""

    @Value("\${preload.role.admin.description}")
    var roleAdminDescription = ""

    @Value("\${preload.team.default.name}")
    var teamDefaultName = ""

}