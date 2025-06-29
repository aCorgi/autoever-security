package com.task.autoeversecurity.util

import java.util.Base64

object CommonUtils {
    fun getBasicAuthToken(
        username: String,
        password: String,
    ): String {
        val credentials = "$username:$password"
        val encodedBasicAuthToken = Base64.getEncoder().encodeToString(credentials.toByteArray(Charsets.UTF_8))

        return "Basic $encodedBasicAuthToken"
    }
}
