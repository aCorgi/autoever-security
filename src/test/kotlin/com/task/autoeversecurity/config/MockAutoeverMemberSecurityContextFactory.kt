package com.task.autoeversecurity.config

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.test.context.support.WithSecurityContextFactory

class MockAutoeverMemberSecurityContextFactory : WithSecurityContextFactory<WithMockAutoeverMember> {
    override fun createSecurityContext(annotation: WithMockAutoeverMember): SecurityContext {
        val autoeverMember =
            AutoeverMember(
                userId = annotation.userId,
                loginId = annotation.loginId,
                roles = annotation.autoeverAuthorities.toList(),
            )

        val authorities =
            autoeverMember.roles
                .map { SimpleGrantedAuthority(it.authority) }

        val authentication =
            UsernamePasswordAuthenticationToken(
                autoeverMember, // principal
                null, // credentials (보통 테스트에서는 null 처리)
                authorities, // 권한 목록
            )

        return SecurityContextHolder.createEmptyContext()
            .apply { this.authentication = authentication }
    }
}
