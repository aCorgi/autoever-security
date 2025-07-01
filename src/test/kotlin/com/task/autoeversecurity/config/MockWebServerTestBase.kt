package com.task.autoeversecurity.config

import okhttp3.mockwebserver.MockWebServer

open class MockWebServerTestBase {
    companion object {
        val kakaoTalkApiMockWebServer: MockWebServer = MockWebServer()
        val smsApiMockWebServer: MockWebServer = MockWebServer()

        init {
            kakaoTalkApiMockWebServer.start()
            smsApiMockWebServer.start()

            System.setProperty("api.kakao-talk.base-url", "http://localhost:${kakaoTalkApiMockWebServer.port}")
            System.setProperty("api.sms.base-url", "http://localhost:${smsApiMockWebServer.port}")
        }
    }
}
