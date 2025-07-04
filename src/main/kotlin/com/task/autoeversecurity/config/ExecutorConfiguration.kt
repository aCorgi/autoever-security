package com.task.autoeversecurity.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Configuration
class ExecutorConfiguration {
    @Bean
    fun executorService(): ExecutorService {
        return Executors.newVirtualThreadPerTaskExecutor()
    }
}
