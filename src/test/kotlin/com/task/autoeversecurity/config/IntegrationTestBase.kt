package com.task.autoeversecurity.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.task.autoeversecurity.AutoeverSecurityApplication
import jakarta.persistence.EntityManager
import okhttp3.mockwebserver.MockResponse
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
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

    private fun openTransaction(): TransactionStatus {
        return transactionManager.getTransaction(DefaultTransactionDefinition())
    }

    private fun closeTransaction(transactionStatus: TransactionStatus) {
        transactionManager.commit(transactionStatus)
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
