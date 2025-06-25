package com.task.autoeversecurity.controller

import com.task.autoeversecurity.dto.UserCreationRequest
import com.task.autoeversecurity.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@Validated
@RestController
@RequestMapping("/users")
class UserController(
    private val userService: UserService,
) {
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    fun join(
        @RequestBody request: UserCreationRequest,
    ) {
        userService.join(request)
    }
}
