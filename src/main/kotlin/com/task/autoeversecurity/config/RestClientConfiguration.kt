package com.task.autoeversecurity.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.task.autoeversecurity.client.KakaoTalkApiClient
import com.task.autoeversecurity.client.SmsApiClient
import com.task.autoeversecurity.exception.ClientBadRequestException
import com.task.autoeversecurity.exception.ExceptionResponse
import com.task.autoeversecurity.exception.InternalServerException
import com.task.autoeversecurity.exception.UnauthorizedException
import com.task.autoeversecurity.property.BasicAuth
import com.task.autoeversecurity.property.KakaoTalkApiProperties
import com.task.autoeversecurity.property.SmsApiProperties
import com.task.autoeversecurity.util.Constants.Exception.DEFAULT_SERVER_EXCEPTION_MESSAGE
import com.task.autoeversecurity.util.logger
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.MediaType
import org.springframework.web.client.RestClient
import org.springframework.web.client.support.RestClientAdapter
import org.springframework.web.service.invoker.HttpServiceProxyFactory

@Configuration
class RestClientConfiguration(
    private val objectMapper: ObjectMapper,
    private val kakaoTalkApiProperties: KakaoTalkApiProperties,
    private val smsApiProperties: SmsApiProperties,
) {
    private val log = logger<RestClientConfiguration>()

    @Bean
    fun kakaoTalkApiClient(): KakaoTalkApiClient {
        val apiClient =
            createBasicAuthRestClient(
                baseUrl = kakaoTalkApiProperties.baseUrl,
                basicAuth = kakaoTalkApiProperties.basicAuth,
            )
                .mutate()
                .defaultHeaders {
                    it.contentType = MediaType.APPLICATION_JSON
                }
                .build()

        val factory =
            HttpServiceProxyFactory.builder()
                .exchangeAdapter(RestClientAdapter.create(apiClient))
                .build()
        return factory.createClient(KakaoTalkApiClient::class.java)
    }

    @Bean
    fun smsApiClient(): SmsApiClient {
        val apiClient =
            createBasicAuthRestClient(
                baseUrl = smsApiProperties.baseUrl,
                basicAuth = smsApiProperties.basicAuth,
            )
                .mutate()
                .defaultHeaders {
                    it.contentType = MediaType.APPLICATION_FORM_URLENCODED
                }
                .build()

        val factory =
            HttpServiceProxyFactory.builder()
                .exchangeAdapter(RestClientAdapter.create(apiClient))
                .build()
        return factory.createClient(SmsApiClient::class.java)
    }

    private fun createBasicAuthRestClient(
        baseUrl: String,
        basicAuth: BasicAuth,
    ): RestClient {
        return RestClient.builder()
            .baseUrl(baseUrl)
            .defaultHeaders {
                it.contentType = MediaType.APPLICATION_JSON
                it.setBasicAuth(basicAuth.username, basicAuth.password)
            }
            .defaultStatusHandler(HttpStatusCode::isError) { _, response ->
                val responseBodyBytes = response.body.readBytes()
                val apiResponseExceptionMessage =
                    try {
                        objectMapper.readValue<ExceptionResponse>(responseBodyBytes).message
                    } catch (e: Exception) {
                        log.warn("External Api Error ${String(responseBodyBytes)}")
                        String(responseBodyBytes)
                    }

                when (response.statusCode) {
                    HttpStatus.INTERNAL_SERVER_ERROR -> {
                        log.error("Internal Api Error $apiResponseExceptionMessage")
                        throw InternalServerException(DEFAULT_SERVER_EXCEPTION_MESSAGE)
                    }
                    HttpStatus.BAD_REQUEST -> throw ClientBadRequestException(apiResponseExceptionMessage)
                    HttpStatus.UNAUTHORIZED -> throw UnauthorizedException(apiResponseExceptionMessage)
                    else -> throw ClientBadRequestException(apiResponseExceptionMessage)
                }
            }
            .build()
    }
}
