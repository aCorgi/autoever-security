package com.task.autoeversecurity.exception

import com.task.autoeversecurity.util.Constants.Exception.DEFAULT_CLIENT_EXCEPTION_MESSAGE
import com.task.autoeversecurity.util.Constants.Exception.DEFAULT_SERVER_EXCEPTION_MESSAGE
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ExceptionHandler {
    @ExceptionHandler(ClientException::class)
    fun handleClientException(clientException: ClientException): ResponseEntity<ExceptionResponse> {
        val responseStatus =
            clientException::class.annotations.find { annotation ->
                annotation is ResponseStatus
            } as? ResponseStatus

        return ResponseEntity.status(responseStatus?.value ?: HttpStatus.BAD_REQUEST)
            .body(ExceptionResponse(message = clientException.message ?: DEFAULT_CLIENT_EXCEPTION_MESSAGE))
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentValidationException(methodArgumentNotValidException: MethodArgumentNotValidException): ResponseEntity<String> {
        return ResponseEntity.badRequest().body(methodArgumentNotValidException.message)
    }

    @ExceptionHandler(Throwable::class)
    fun handleUnrecognizedThrowable(throwable: Throwable): ResponseEntity<ExceptionResponse> {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ExceptionResponse(message = throwable.message ?: DEFAULT_SERVER_EXCEPTION_MESSAGE))
    }
}
