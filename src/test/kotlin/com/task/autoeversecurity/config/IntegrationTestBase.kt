package com.task.autoeversecurity.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.task.autoeversecurity.AutoeverSecurityApplication
import com.task.autoeversecurity.component.MessageSendingProducer
import com.task.autoeversecurity.component.RateLimiter
import com.task.autoeversecurity.property.RabbitMQProperties
import jakarta.persistence.EntityManager
import okhttp3.mockwebserver.MockResponse
import org.junit.jupiter.api.extension.ExtendWith
import org.redisson.api.RedissonClient
import org.springframework.amqp.core.AmqpAdmin
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.TransactionStatus
import org.springframework.transaction.support.DefaultTransactionDefinition
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers
@ContextConfiguration(classes = [AutoeverSecurityApplication::class])
@ActiveProfiles("test")
@ExtendWith(RepositoryContainerExtension::class)
@SpringBootTest
abstract class IntegrationTestBase : MockWebServerTestBase() {
    @Autowired
    protected lateinit var entityManager: EntityManager

    @Autowired
    private lateinit var transactionManager: PlatformTransactionManager

    @Autowired
    protected lateinit var objectMapper: ObjectMapper

    @Autowired
    protected lateinit var rabbitTemplate: RabbitTemplate

    @Autowired
    protected lateinit var rabbitMQProperties: RabbitMQProperties

    @Autowired
    protected lateinit var amqpAdmin: AmqpAdmin

    @Autowired
    protected lateinit var redissonClient: RedissonClient

    @MockitoSpyBean
    protected lateinit var rateLimiter: RateLimiter

    @MockitoSpyBean
    protected lateinit var messageSendingProducer: MessageSendingProducer

    private fun openTransaction(): TransactionStatus {
        return transactionManager.getTransaction(DefaultTransactionDefinition())
    }

    private fun closeTransaction(transactionStatus: TransactionStatus) {
        transactionManager.commit(transactionStatus)
    }

    protected fun <T> transactional(action: () -> T): T {
        val status = openTransaction()
        try {
            return action()
        } finally {
            closeTransaction(status)
        }
    }

    protected fun getSuccessMockResponse(body: String? = null): MockResponse {
        return MockResponse()
            .setResponseCode(HttpStatus.OK.value())
            .also {
                if (body != null) {
                    it.setBody(body)
                }
            }
    }
}
