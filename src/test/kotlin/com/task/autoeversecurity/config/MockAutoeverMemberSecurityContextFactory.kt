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
                autoeverMember,
                null,
                authorities,
            )

        return SecurityContextHolder.createEmptyContext()
            .apply { this.authentication = authentication }
    }
}
