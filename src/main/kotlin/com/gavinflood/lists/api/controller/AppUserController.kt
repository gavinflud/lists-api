package com.gavinflood.lists.api.controller

import com.gavinflood.lists.api.domain.AppUser
import com.gavinflood.lists.api.service.AppUserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * Controller with REST endpoints for user management.
 */
@RestController
@RequestMapping("/api/users")
class AppUserController(private val appUserService: AppUserService) {

    /**
     * Register a user.
     *
     * @param user the user to be registered
     * @return the persisted user
     */
    @PostMapping
    fun register(@RequestBody user: AppUser): ResponseEntity<AppUser> {
        return ResponseEntity.ok(appUserService.create(user))
    }

    /**
     * Find a user.
     *
     * @param id identifies the user
     * @return the user if found
     */
    @GetMapping("/{id}")
    fun get(@PathVariable id: Long): ResponseEntity<AppUser> {
        return ResponseEntity.ok(appUserService.findById(id))
    }

    /**
     * Update a user.
     *
     * @param id identifies the user
     * @param updatedUser the updated representation of the user
     * @return the persisted user
     */
    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody updatedUser: AppUser): ResponseEntity<AppUser> {
        return ResponseEntity.ok(appUserService.update(id, updatedUser))
    }

    /**
     * Retire a user.
     *
     * @param id identifies the user
     */
    @DeleteMapping("/{id}")
    fun retire(@PathVariable id: Long): ResponseEntity<*> {
        return ResponseEntity.ok(appUserService.retire(id))
    }

}