package com.task.autoeversecurity

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@ConfigurationPropertiesScan
@SpringBootApplication
class AutoeverSecurityApplication

fun main(args: Array<String>) {
    runApplication<AutoeverSecurityApplication>(*args)
}
