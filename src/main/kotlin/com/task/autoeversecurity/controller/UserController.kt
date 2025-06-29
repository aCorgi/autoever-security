package com.task.autoeversecurity.controller

import com.task.autoeversecurity.config.AutoeverMember
import com.task.autoeversecurity.dto.MyselfUserResponse
import com.task.autoeversecurity.dto.UserJoinRequest
import com.task.autoeversecurity.dto.UserLoginRequest
import com.task.autoeversecurity.service.UserService
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@Validated
@RestController
@RequestMapping("/users")
@SecurityRequirement(name = "basicAuth")
class UserController(
    private val userService: UserService,
) {
    @GetMapping("/myself")
    fun getMyself(
        @AuthenticationPrincipal autoeverMember: AutoeverMember,
    ): MyselfUserResponse {
        return userService.getMyself(autoeverMember)
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/join")
    fun join(
        @RequestBody request: UserJoinRequest,
    ) {
        userService.join(request)
    }

    @PostMapping("/login")
    fun login(
        @RequestBody request: UserLoginRequest,
    ): String {
        return userService.login(request)
    }
}
