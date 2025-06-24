package com.task.autoeversecurity.service

import com.task.autoeversecurity.config.UnitTestBase
import org.mockito.InjectMocks

class AuthServiceTest : UnitTestBase() {
    @InjectMocks
    private lateinit var authService: AuthService
}
