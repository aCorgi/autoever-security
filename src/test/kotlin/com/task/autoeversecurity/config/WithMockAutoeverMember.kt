package com.task.autoeversecurity.config

import org.springframework.security.test.context.support.WithSecurityContext

@Retention(AnnotationRetention.RUNTIME)
@WithSecurityContext(factory = MockAutoeverMemberSecurityContextFactory::class)
annotation class WithMockAutoeverMember(
    val userId: Int = 30000,
    val loginId: String = "banner4@naver.com",
    val autoeverAuthorities: Array<AutoeverAuthority> = [],
    val name: String = "이해원",
)
