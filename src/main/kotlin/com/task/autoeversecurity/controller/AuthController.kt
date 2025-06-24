package com.task.autoeversecurity.controller

import com.task.autoeversecurity.service.AuthService
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Validated
@RestController
@RequestMapping("/auth")
class AuthController(
    private val authService: AuthService,
)
