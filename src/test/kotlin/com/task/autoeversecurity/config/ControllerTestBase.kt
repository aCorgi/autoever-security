package com.task.autoeversecurity.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.task.autoeversecurity.AutoeverSecurityApplication
import com.task.autoeversecurity.controller.UserController
import com.task.autoeversecurity.exception.ExceptionHandler
import com.task.autoeversecurity.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.ImportAutoConfiguration
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc

@WebMvcTest(
    value = [
        UserController::class,
    ],
)
@ActiveProfiles("test")
@ImportAutoConfiguration
@ComponentScan(basePackages = ["com.task.autoeversecurity.controller"])
@ContextConfiguration(
    classes = [
        AutoeverSecurityApplication::class,
        ObjectMapperConfiguration::class,
        ExceptionHandler::class,
    ],
)
abstract class ControllerTestBase {
    @Autowired
    protected lateinit var mockMvc: MockMvc

    @Autowired
    protected lateinit var objectMapper: ObjectMapper

    @MockitoBean
    protected lateinit var userService: UserService
}
