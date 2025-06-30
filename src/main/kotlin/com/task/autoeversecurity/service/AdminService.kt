package com.task.autoeversecurity.service

import com.task.autoeversecurity.component.MessageSendingProducer
import com.task.autoeversecurity.dto.AgeGroup
import com.task.autoeversecurity.dto.UserResponse
import com.task.autoeversecurity.dto.UserUpdateRequest
import com.task.autoeversecurity.dto.api.SendKakaoTalkApiRequest
import com.task.autoeversecurity.dto.message.SendKakaoTalkMessageDto
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional(readOnly = true)
@Service
class AdminService(
    private val userService: UserService,
    private val passwordEncoder: PasswordEncoder,
    private val messageSendingProducer: MessageSendingProducer,
) {
    fun sendKakaoMessageByAgeGroup(ageGroup: AgeGroup) {
        val users = userService.findByAgeGroup(ageGroup)

        users.forEach { user ->
            val request =
                SendKakaoTalkApiRequest(
                    phone = user.phoneNumber,
                    message =
                        """
                        , 안녕하세요. 현대 오토에버입니다.
                        ${ageGroup.name} 연령대이신 ${user.name}님만을 위한 특별한 소식을 전해드립니다.
                        그건 바로, 이해원 지원자가 과제를 거의 다 마무리했다는 것입니다.
                        """.trimIndent(),
                )

            messageSendingProducer.sendKakaoTalkMessage(
                SendKakaoTalkMessageDto(
                    phone = request.phone,
                    message = request.message,
                ),
            )
        }
    }

    fun getPagedUsers(pageable: Pageable): Page<UserResponse> {
        return userService.getPagedUsers(pageable)
            .map { UserResponse(it) }
    }

    @Transactional
    fun updateUser(request: UserUpdateRequest) {
        val user = userService.findById(request.userId)

        request.password?.let {
            user.updatePassword(password = passwordEncoder.encode(it))
        }

        request.address?.let {
            user.updateAddress(it.toEmbeddable())
        }
    }

    @Transactional
    fun deleteUser(userId: Int) {
        userService.deleteById(userId)
    }
}
